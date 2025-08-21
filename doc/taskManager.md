# 任务管理模块（Spec v1.2）

运行环境：MySQL 8+；不使用外键；所有删除均为逻辑删除 del\_flag。
公共审计字段：create\_by / create\_time / update\_by / update\_time（VARCHAR(32) / DATETIME）。
身份与权限：依赖 sys\_user / sys\_role / sys\_user\_role。
关键约定：模板连边用模板节点ID；tc\_task\_node\_inst 包含 handler\_role\_ids（JSON 数组）。

---

## 0. 业务概述

任务管理支持：创建任务 → 查看四个 Tab → 处理节点 → 完成/取消任务 → 记录操作。
工作流来源于模板：tc\_todo\_template（外部） + tc\_task\_template\_node（仅结构与时限）。
实例化产物：tc\_task（任务）、tc\_task\_node\_inst（节点实例，含办理角色快照）、tc\_task\_work\_item（本地工作项）、tc\_task\_node\_action\_record（操作记录）。

四个 Tab（非管理员）：发起的 / 待办 / 参与 / 已处理（我所属角色组有人处理过）。
管理员视图：全量任务。

---

## 1. 数据模型（实现关键字段）

DDL 已在总文档（画布）中，这里只列编码相关字段与含义。

### 1.1 模板域（只读）

* **tc\_todo\_template**：模板主表（外部维护）
* **tc\_task\_template\_node**：模板节点（不含办理角色）

  * id：模板节点ID（唯一，用于前驱/后继连边键）
  * template\_id：模板ID（string，必填）
  * node\_id：节点定义ID（tc\_node\_info.id）
  * order\_no：层序（可空）
  * prev\_keys / next\_keys：数组，存放模板节点ID
  * max\_duration：最大时长（分钟）

### 1.2 节点定义域（配置中心）

* **tc\_node\_info**：节点定义（actions JSON、启停状态、时长等）
* **tc\_node\_role\_rel**：节点与角色多对多（用于实例化时生成办理角色快照）

### 1.3 任务运行域（运行态）

* **tc\_task**
  task\_code, task\_name, task\_requirement(<=255), template\_id(string，必填)
  need\_imaging, imaging\_area(JSON|null), result\_display\_needed
  satellites(JSON 二级结构), remote\_cmds(JSON), orbit\_plans(JSON)
  status：0运行中 / 1结束 / 2异常结束 / 3取消

* **tc\_task\_node\_inst**
  task\_id, template\_id(string，必填), node\_id
  prev\_node\_ids(JSON) / next\_node\_ids(JSON)：实例ID数组
  arrived\_count：到达前驱计数
  handler\_role\_ids(JSON)：办理角色ID数组快照
  order\_no、status(0待处理/1处理中/2已完成/3已取消)
  started\_at, completed\_at, completed\_by, max\_duration

* **tc\_task\_work\_item**
  task\_id, node\_inst\_id, assignee\_id
  phase\_status：
  0候选未到达 / 1待办 / 2我已处理 / 3任务取消撤销 / 4他人已完成失效

* **tc\_task\_node\_action\_record**
  task\_id, node\_inst\_id, action\_type, action\_payload(JSON), create\_by, create\_time
  action\_payload.attachments：\[{ filename, url, sizeBytes?, contentType? }]

---

## 2. 核心流程与算法（可编程）

### 2.1 新建任务（实例化工作流）

**入参（JSON）**

taskCode\*, taskName\*, taskRequirement, templateId\*,
needImaging(0/1), imagingArea(JSON|null), resultDisplayNeeded(0/1),
satellites(JSON), remoteCmds(JSON), orbitPlans(JSON)。

**事务流程（伪代码）**

```sql
BEGIN;

-- A. 写任务主表
INSERT INTO tc_task(..., status=0, create_by=:uid) VALUES(...);
taskId := LAST_INSERT_ID();

-- B. 读模板节点（含模板节点ID）
tplNodes := SELECT * FROM tc_task_template_node WHERE template_id=:templateId AND del_flag=0;

-- C. 第1遍：创建实例，建立 “模板节点ID → 实例ID” 映射
for each tpl in tplNodes:
  -- 从定义域生成办理角色快照
  roles := SELECT role_id FROM tc_node_role_rel WHERE node_id=tpl.node_id AND del_flag=0;
  INSERT INTO tc_task_node_inst(
    task_id, template_id, node_id,
    prev_node_ids='[]', next_node_ids='[]',
    arrived_count=0,
    handler_role_ids = JSON_ARRAY(roles...),
    order_no=tpl.order_no,
    status=0,
    max_duration=tpl.max_duration,
    create_by=:uid
  );
  mapTplToInst[tpl.id] = LAST_INSERT_ID();

-- D. 第2遍：用模板节点ID映射回填 prev/next 为实例ID数组
for each tpl in tplNodes:
  instId   := mapTplToInst[tpl.id];
  prevInst := [ mapTplToInst[x] for x in (tpl.prev_keys or []) ];
  nextInst := [ mapTplToInst[x] for x in (tpl.next_keys or []) ];
  UPDATE tc_task_node_inst
  SET prev_node_ids=JSON_ARRAY(prevInst...), next_node_ids=JSON_ARRAY(nextInst...)
  WHERE id=instId;

-- E. 追加“查看影像结果”（当 result_display_needed=1）
if result_display_needed == 1:
  endInstIds := SELECT id FROM tc_task_node_inst
                WHERE task_id=:taskId AND del_flag=0 AND JSON_LENGTH(next_node_ids)=0;
  -- 预置“查看影像结果” node_id（来自 tc_node_info）
  rolesForView := SELECT role_id FROM tc_node_role_rel WHERE node_id=:VIEW_NODE_ID AND del_flag=0;
  INSERT INTO tc_task_node_inst(
    task_id, template_id, node_id,
    prev_node_ids=JSON_ARRAY(endInstIds...), next_node_ids='[]',
    arrived_count=0,
    handler_role_ids = JSON_ARRAY(rolesForView...),
    order_no=NULL, status=0, max_duration=NULL, create_by=:uid
  );
  viewInstId := LAST_INSERT_ID();
  -- 让所有末端指向 viewInstId
  for id in endInstIds:
    UPDATE tc_task_node_inst
    SET next_node_ids = JSON_ARRAY_APPEND(next_node_ids, '$', viewInstId)
    WHERE id=id;

-- F. 激活起点（prev 为空 → 置处理中）并生成“待办”
startNodes := SELECT * FROM tc_task_node_inst
              WHERE task_id=:taskId AND del_flag=0 AND JSON_LENGTH(prev_node_ids)=0;

for each n in startNodes:
  UPDATE tc_task_node_inst SET status=1, started_at=NOW()
  WHERE id=n.id AND status=0 AND del_flag=0;

  -- 用实例快照的 handler_role_ids → 找用户 → 生成待办
  roleIds := JSON_EXTRACT(n.handler_role_ids, '$');
  users := SELECT DISTINCT ur.user_id FROM sys_user_role ur
           WHERE JSON_CONTAINS(roleIds, CAST(ur.role_id AS JSON), '$');
  for u in users:
    INSERT INTO tc_task_work_item(task_id, node_inst_id, assignee_id, phase_status, create_by)
    VALUES (:taskId, n.id, u, 1, :uid);  -- 1=待办

COMMIT;
```

备注：handler\_role\_ids 为办理角色的快照，避免后续定义域变化影响已生成任务。

---

### 2.2 任务列表（五个 Tab，更新版）

#### 2.2.1 Tab & 访问控制

Tab 值：
- **all** — 所有任务（TODO：后续加管理员判定）
- **startedByMe** — 发起的任务（创建人为我）
- **todo** — 待办任务（我的办理项，phase_status=1）
- **participated** — 参与任务（候选未到达，phase_status=0）
- **handled** — 已处理任务（我本人或我所属角色组有人处理过，phase_status=2）

TODO：当管理员判定规则明确后，在 tab=all 的入口做权限校验。

#### 2.2.2 统一筛选参数（全部可选）

| 参数名        | 类型     | 必填 | 说明                                   |
|---------------|----------|------|--------------------------------------|
| tab           | string   | 是   | 枚举：all / startedByMe / todo / participated / handled |
| q             | string   | 否   | 模糊搜索：任务名称或任务编码          |
| status        | int      | 否   | 任务状态：0运行中，1结束，2异常结束，3取消 |
| templateId    | string   | 否   | 任务类型（模板ID）                    |
| startTimeFrom | datetime | 否   | 创建时间起（含）                      |
| page          | int      | 否   | 页码，默认 1                         |
| pageSize      | int      | 否   | 每页条数，默认 10                     |

#### 2.2.3 统一返回字段（对象化）

```json
{
  "taskId": 123,
  "taskName": "应急成像-京津冀",
  "taskCode": "T2025-0001",
  "template": { "templateId": "TPL-0004", "templateName": "应急成像模板" },
  "satellites": [{"group":"三体星座","satIds":[1,2]}],
  "createTime": "2025-08-01 10:20:30",
  "status": 0,
  "currentNodes": [
    {
      "nodeInstId": 8881,
      "nodeName": "飞控准备",
      "roles": [
        {"roleId":"1","roleName":"总体部"},
        {"roleId":"3","roleName":"测运控组组长"}
      ]
    },
    {
      "nodeInstId": 8882,
      "nodeName": "资源确认",
      "roles": [
        {"roleId":"3","roleName":"测运控组组长"}
      ]
    }
  ]
}
````

#### 2.2.4 接口设计（单接口 + tab 参数）

* **GET /task/list**

说明：统一入口；通过 tab 区分五种列表；tab=all 暂不做权限拦截（TODO）。
Query 参数：见 §2.2.2。
返回：`{ list: [...如上对象...], total: number }`

#### 2.2.5 SQL 参考实现（MySQL 8；JSON 聚合）

说明：为返回“模板对象 + 当前节点对象数组”，采用 JSON\_OBJECT/JSON\_ARRAYAGG。
可用 MyBatis/QueryDSL 动态拼接 where、分页（LIMIT ?,?）。
如果 sys\_role.id 为 varchar，则 handler\_role\_ids 中也存字符串；若为数值，请在 JSON\_TABLE 里按需 CAST。

**公共：模板对象、当前激活节点对象数组（可复用子查询）**

```sql
/* 模板对象：{templateId, templateName} */
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id

/* 当前激活节点对象数组： */
LEFT JOIN (
  SELECT
    ni.task_id,
    JSON_ARRAYAGG(
      JSON_OBJECT(
        'nodeInstId', ni.id,
        'nodeName', ninfo.name,
        'roles',
          (
            SELECT JSON_ARRAYAGG(JSON_OBJECT('roleId', r.id, 'roleName', r.role_name))
            FROM JSON_TABLE(ni.handler_role_ids, '$[*]' COLUMNS (role_id VARCHAR(64) PATH '$')) jt
            JOIN sys_role r ON r.id = jt.role_id
          )
      )
      ORDER BY ni.order_no, ni.id
    ) AS current_nodes_json
  FROM tc_task_node_inst ni
  JOIN tc_node_info ninfo ON ninfo.id = ni.node_id
  WHERE ni.del_flag = 0 AND ni.status = 1
  GROUP BY ni.task_id
) curN ON curN.task_id = t.id
```

**公共：筛选（所有 Tab 复用）**

```sql
WHERE t.del_flag = 0
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom )
```

**公共：SELECT 字段（统一）**

```sql
SELECT
  t.id                           AS taskId,
  t.task_name                    AS taskName,
  t.task_code                    AS taskCode,
  JSON_OBJECT(
    'templateId',   t.template_id,
    'templateName', tt.template_name
  )                              AS template,
  t.satellites                   AS satellites,
  t.create_time                  AS createTime,
  t.status                       AS status,
  COALESCE(curN.current_nodes_json, JSON_ARRAY()) AS currentNodes
```

**A) tab = all（所有任务；TODO 后续加管理员判定）**

```sql
SELECT_FIELDS
FROM tc_task t
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id
LEFT JOIN curN_subquery curN ON curN.task_id = t.id
WHERE_CONDITIONS
ORDER BY t.create_time DESC
LIMIT :offset, :pageSize;

/* 统计 total */
SELECT COUNT(1)
FROM tc_task t
WHERE_CONDITIONS;
```
**B) tab = startedByMe**

```sql
SELECT_FIELDS
FROM tc_task t
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id
LEFT JOIN curN_subquery curN ON curN.task_id = t.id
WHERE_CONDITIONS
  AND t.create_by = :userId
ORDER BY t.create_time DESC
LIMIT :offset, :pageSize;

SELECT COUNT(1)
FROM tc_task t
WHERE_CONDITIONS
  AND t.create_by = :userId;

```
**C) tab = todo（我的待办）**

```sql
SELECT_FIELDS
FROM tc_task t
JOIN tc_task_work_item w
  ON w.task_id = t.id
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id
LEFT JOIN curN_subquery curN ON curN.task_id = t.id
WHERE t.del_flag=0
  AND w.del_flag=0
  AND w.assignee_id = :userId
  AND w.phase_status = 1
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom )
ORDER BY t.create_time DESC
LIMIT :offset, :pageSize;

SELECT COUNT(DISTINCT t.id)
FROM tc_task t
JOIN tc_task_work_item w ON w.task_id=t.id
WHERE t.del_flag=0
  AND w.del_flag=0
  AND w.assignee_id = :userId
  AND w.phase_status = 1
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom );

```

**D) tab = participated（候选未到达）**

```sql
SELECT_FIELDS
FROM tc_task t
JOIN tc_task_work_item w
  ON w.task_id = t.id
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id
LEFT JOIN curN_subquery curN ON curN.task_id = t.id
WHERE t.del_flag=0
  AND w.del_flag=0
  AND w.assignee_id = :userId
  AND w.phase_status = 0
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom )
ORDER BY t.create_time DESC
LIMIT :offset, :pageSize;

SELECT COUNT(DISTINCT t.id)
FROM tc_task t
JOIN tc_task_work_item w ON w.task_id=t.id
WHERE t.del_flag=0
  AND w.del_flag=0
  AND w.assignee_id = :userId
  AND w.phase_status = 0
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom );

```
**E) tab = handled（我或我组里的人处理过）**

```sql
WITH my_roles AS (
  SELECT DISTINCT role_id
  FROM sys_user_role
  WHERE user_id = :userId
),
my_group_users AS (
  SELECT DISTINCT sur.user_id
  FROM sys_user_role sur
  WHERE sur.role_id IN (SELECT role_id FROM my_roles)
)
SELECT_FIELDS
FROM tc_task t
JOIN tc_task_work_item wi ON wi.task_id = t.id
LEFT JOIN tc_todo_template tt ON tt.template_id = t.template_id
LEFT JOIN curN_subquery curN ON curN.task_id = t.id
WHERE t.del_flag=0
  AND wi.del_flag=0
  AND wi.phase_status  IN (2,4)
  AND w.assignee_id = :userId
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom )
GROUP BY t.id, t.task_name, t.task_code, t.template_id, tt.template_name, t.satellites, t.create_time, t.status, curN.current_nodes_json
ORDER BY t.create_time DESC
LIMIT :offset, :pageSize;

SELECT COUNT(DISTINCT t.id)
FROM tc_task t
JOIN tc_task_work_item wi ON wi.task_id=t.id
WHERE t.del_flag=0
  AND wi.del_flag=0
  AND wi.phase_status  IN (2,4)
  AND w.assignee_id = :userId
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom );

```
注：上面 SELECT_FIELDS / WHERE_CONDITIONS / curN_subquery 为示意占位，实际写 SQL 时请替换成完整片段。
#### 2.2.6 小结

* 单接口 GET /task/list 支持五个 Tab。
* 筛选项全可选，时间仅“起”。
* 返回结构对象化：模板对象、当前激活节点对象（含实例ID、名称、角色数组）。
* tab=all 暂不做管理员限制（已标注 TODO）。
* SQL 使用 JSON 聚合，后端直接返回最终结构，前端零拼装。


### 2.3 节点办理（提交动作 → 完成 → 推进后继）

提交 & 完成（事务）

```sql
BEGIN;

-- A. 记录一次操作（可含附件）
INSERT INTO tc_task_node_action_record(task_id,node_inst_id,action_type,action_payload,create_by)
VALUES (:taskId,:nodeInstId,:type,:payloadJson,:uid);

-- B. 完成节点（行锁 + 幂等）
UPDATE tc_task_node_inst
SET status=2, completed_at=NOW(), completed_by=:uid, update_by=:uid, update_time=NOW()
WHERE id=:nodeInstId AND task_id=:taskId AND status=1 AND del_flag=0;

-- C. 工作项：我=2已处理；其他人=4他人已完成失效
UPDATE tc_task_work_item
SET phase_status=2, update_by=:uid, update_time=NOW()
WHERE task_id=:taskId AND node_inst_id=:nodeInstId AND assignee_id=:uid AND del_flag=0;

UPDATE tc_task_work_item
SET phase_status=4, update_by=:uid, update_time=NOW()
WHERE task_id=:taskId AND node_inst_id=:nodeInstId AND assignee_id<>:uid AND del_flag=0 AND phase_status IN (0,1);

-- D. 推进后继：计数 + 激活 + 发放待办（用实例快照）
nextIds := SELECT JSON_EXTRACT(next_node_ids, '$') FROM tc_task_node_inst WHERE id=:nodeInstId FOR UPDATE;
for each nextId in nextIds:
  UPDATE tc_task_node_inst
  SET arrived_count = arrived_count + 1, update_by=:uid, update_time=NOW()
  WHERE id=nextId AND del_flag=0;

  UPDATE tc_task_node_inst
  SET status=1, started_at=NOW(), update_by=:uid
  WHERE id=nextId AND status=0 AND del_flag=0
    AND arrived_count >= JSON_LENGTH(prev_node_ids);

  IF ROW_COUNT() > 0 THEN
     roleIds := (SELECT handler_role_ids FROM tc_task_node_inst WHERE id=nextId);
     users := SELECT DISTINCT ur.user_id FROM sys_user_role ur
              WHERE JSON_CONTAINS(roleIds, CAST(ur.role_id AS JSON), '$');
     for u in users:
       INSERT INTO tc_task_work_item(task_id,node_inst_id,assignee_id,phase_status,create_by)
       VALUES (:taskId, nextId, u, 1, :uid);
  END IF;

-- E. 任务完结判定（无任何 0/1 节点）
SELECT COUNT(*) INTO @ongoing
FROM tc_task_node_inst
WHERE task_id=:taskId AND del_flag=0 AND status IN (0,1);
IF @ongoing = 0 THEN
  UPDATE tc_task SET status=1, update_by=:uid, update_time=NOW()
  WHERE id=:taskId AND del_flag=0;
END IF;

COMMIT;
```

权限校验（服务层）

* 节点必须 status=1；
* uid 必须属于当前实例 handler\_role\_ids 对应的角色之一：

```sql
EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id=:uid AND JSON_CONTAINS(ni.handler_role_ids, CAST(ur.role_id AS JSON), '$'));
```

---

### 2.4 附件负荷示例

上传：

```json
{
  "comment": "补充材料",
  "attachments": [
    {"filename": "A报告.pdf", "url": "https://.../a.pdf", "sizeBytes": 123456, "contentType": "application/pdf"}
  ]
}
```

决策：

```json
{"decision":"yes","comment":"同意通过"}
```

文本：

```json
{"text":"执行记录……"}
```

---

### 2.5 取消任务（只要“尚无人完成任何节点”）

允许起点在创建时即激活为“处理中(1)”（形成待办并开始计时）。
取消条件：任务 status=0 且不存在任何节点 status=2。

**事务脚本**

```sql
BEGIN;

SELECT status FROM tc_task WHERE id=:taskId AND del_flag=0 FOR UPDATE;

SELECT COUNT(*) AS done_cnt
FROM tc_task_node_inst
WHERE task_id=:taskId AND del_flag=0 AND status=2
FOR UPDATE;

IF done_cnt = 0 THEN
  UPDATE tc_task
  SET status=3, update_by=:uid, update_time=NOW()
  WHERE id=:taskId AND del_flag=0;

  UPDATE tc_task_node_inst
  SET status=3, update_by=:uid, update_time=NOW()
  WHERE task_id=:taskId AND del_flag=0 AND status IN (0,1);

  UPDATE tc_task_work_item
  SET phase_status=3, update_by=:uid, update_time=NOW()
  WHERE task_id=:taskId AND del_flag=0 AND phase_status IN (0,1);
END IF;

COMMIT;
```

---

### 2.6 超时提醒（定时任务）

目标：status=1 且 max\_duration 不空
判定：NOW() > started\_at + INTERVAL max\_duration MINUTE
触发：写消息/事件或直接推送；可附带 task\_id / node\_inst\_id / assignees（由实例 handler\_role\_ids → sys\_user\_role 解析）

---

## 3. 接口（REST）

### 3.1 任务

创建任务 `POST /task/create`
Body(JSON)：

```
taskCode*, taskName*, taskRequirement, templateId*,
needImaging(0|1), imagingArea, resultDisplayNeeded(0|1),
satellites, remoteCmds, orbitPlans
```

Resp：`{ id, taskCode }`

取消任务 `POST /task/cancel?taskId=...`
Resp：`{ success: true }`（不满足条件报 TASK\_CANNOT\_CANCEL）

任务详情 `GET /task/detail?taskId=...`
Resp：任务主信息 + 当前激活节点ID集合（可选）

列表（四个 Tab）

* 发起的：`GET /task/list/startedByMe?userId&page&pageSize`
* 待办： `GET /task/list/todo?userId&page&pageSize`
* 参与： `GET /task/list/participated?userId&page&pageSize`
* 已处理：`GET /task/list/handledByMyGroup?userId&page&pageSize`

Resp（统一分页）：`{ list: [...], total: number }`

### 3.2 节点办理

提交动作 `POST /task/node/submit`
Body(JSON)：

```
taskId*, nodeInstId*, actionType* (0/1/2/3), actionPayload* (JSON)
```

行为：按 §2.3 完成节点、推进后继、发放待办。
Resp：`{ success: true }`

节点操作记录 `GET /task/node/actions?nodeInstId=...`
Resp：`[{ id, actionType, actionPayload, createBy, createTime }]`

---


## 4. 常用 SQL 片段

**当前激活节点（列表提示）**

```sql
SELECT t.id,
       GROUP_CONCAT(n.id ORDER BY n.order_no, n.id) AS current_node_ids_csv
FROM tc_task t
LEFT JOIN tc_task_node_inst n
  ON n.task_id=t.id AND n.del_flag=0 AND n.status=1
WHERE t.del_flag=0
GROUP BY t.id;
```

**节点实例的“可办理人”**

```sql
SELECT DISTINCT ur.user_id
FROM tc_task_node_inst ni
JOIN sys_user_role ur
  ON JSON_CONTAINS(ni.handler_role_ids, CAST(ur.role_id AS JSON), '$')
WHERE ni.id = :nodeInstId;
```

**节点附件清单**

```sql
SELECT r.id AS action_record_id, a.filename, a.url
FROM tc_task_node_action_record r,
JSON_TABLE(r.action_payload, '$.attachments[*]' COLUMNS (
  filename VARCHAR(255) PATH '$.filename',
  url      VARCHAR(500) PATH '$.url'
)) a
WHERE r.node_inst_id = :nodeInstId
  AND r.del_flag = 0
ORDER BY r.create_time DESC;
```

---

## 5. 错误码与校验

* **400 PARAM\_INVALID**：JSON 非法、必填缺失
* **403 NO\_PERMISSION**：提交者不在实例 handler\_role\_ids 对应角色集合中
* **404 NOT\_FOUND**：任务/节点不存在
* **409 TASK\_CANNOT\_CANCEL**：已有节点完成，不能取消
* **409 NODE\_NOT\_PROCESSING**：节点不是处理中，不能提交
* **500 INTERNAL\_ERROR**

**校验点：**

* 创建：templateId 有效；模板节点非空；satellites/remote\_cmds/orbit\_plans 均 JSON\_VALID=1。
* 提交：权限校验基于实例 handler\_role\_ids。
* 取消：无 status=2 的节点。

---

## 6. 并发与幂等

* 节点完成 `UPDATE ... WHERE status=1` 控制一次性；
* 后继激活使用 `arrived_count + status=0 + arrived_count >= JSON_LENGTH(prev_node_ids)`，可重放幂等；
* 办理（提交→完成→推进后继→完结判定）放单事务中。

---

## 7. 开发清单（codeX 任务拆分）

**DAO**：TaskDao / TaskNodeInstDao / TaskWorkItemDao / TaskActionRecordDao / TemplateNodeDao

**Service**：

* TaskService.createTask(dto)（§2.1）
* TaskService.cancel(taskId, uid)（§2.5）
* TaskService.listTabs(userId, type, page, size)（§2.2）
* NodeService.submitAction(dto)（§2.3）

**校验器**：JSON 校验、实例角色权限校验

**Scheduler**：超时提醒扫描（§2.6）

**API**：按 §3 暴露

**测试**：

* 起点激活与待办生成
* 合流（多前驱）激活
* 节点完成推进后继 & 任务完结
* 取消约束
* 附件解析
* 同一 node\_id 在模板多次出现（模板节点ID连边）
