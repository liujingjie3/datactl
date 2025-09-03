# 任务管理模块（Spec v1.0）

运行环境：MySQL 8+；所有删除均为逻辑删除 del\_flag。
公共审计字段：create\_by / create\_time / update\_by / update\_time（VARCHAR(32) / DATETIME，存储用户ID）。
身份与权限：依赖 sys\_user / sys\_role / sys\_user\_role。

---

## 0. 业务概述

任务管理支持：创建任务 → 查看四个 Tab → 处理节点 → 完成/取消任务 → 记录操作->事件通知。
工作流来源于模板：tc\_todo\_template.template\_attr（外部维护，包含节点结构、角色与时限）。
实例化产物：tc\_task（任务）、tc\_task\_node\_inst（节点实例，含办理角色快照）、tc\_task\_work\_item（本地工作项）、tc\_task\_node\_action\_record（操作记录）。

四个 Tab（非管理员）：发起的 / 待办  / 已处理（我处理或者我所属角色组有人处理过或者该工作流异常）。
管理员视图：全量任务。

---

## 1. 数据模型（实现关键字段）

DDL 详见ddl.md。

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
  satellites(JSON 二级结构，如 [{"group":"阿联酋星座","satIds":["4","5"]}]), remote\_cmds(JSON), orbit\_plans(JSON)
  status：0运行中 / 1结束 / 2异常结束 / 3取消

* **tc\_task\_node\_inst**
  task\_id, template\_id(string，必填), node\_id
  prev\_node\_ids(JSON) / next\_node\_ids(JSON)：实例ID数组
  arrived\_count：到达前驱计数
  handler\_role\_ids(JSON)：办理角色ID数组快照
  order\_no、status(0待处理/1处理中/2已完成/3已取消/4异常结束)
  started\_at, completed\_at, completed\_by, max\_duration

* **tc\_task\_work\_item**
  task\_id, node\_inst\_id, assignee\_id
  phase\_status：
  0候选未到达 / 1待办 / 2我已处理 / 3任务取消撤销 / 4他人已完成失效 / 5异常结束

* **tc\_task\_node\_action\_record**
  task\_id, node\_inst\_id, action\_type, action\_payload(JSON), create\_by, create\_time
  action\_payload.attachments：\[{ filename, url, sizeBytes?, contentType? }]

---

## 2. 核心流程与算法

### 2.1 新建任务（实例化工作流）

**入参（JSON）**

taskName\*, taskRequirement\*, templateId\*,
needImaging(0/1), imagingArea(JSON|null), resultDisplayNeeded(0/1),
satellites(JSON，如 [{"group":"阿联酋星座","satIds":["4","5"]}]), remoteCmds(List<RemoteCmdExportVO>), orbitPlans(List<OrbitPlanExportVO>)。

**事务流程（伪代码）**

```sql
BEGIN;

-- A. 写任务主表
INSERT INTO tc_task(..., status=0, create_by=:uid) VALUES(...);
taskId := LAST_INSERT_ID();

attr := SELECT template_attr FROM tc_todo_template WHERE template_id=:templateId AND del_flag=0;
tplNodes := JSON_PARSE(attr);  -- 每个元素包含 key/nodeId/prev/next/roleIds/maxDuration/orderNo

-- C. 第1遍：创建实例，建立 “模板节点ID → 实例ID” 映射
for each tpl in tplNodes:
  INSERT INTO tc_task_node_inst(
    task_id, template_id, node_id,
    prev_node_ids='[]', next_node_ids='[]',
    arrived_count=0,
    handler_role_ids = JSON_ARRAY(tpl.roleIds...),
    order_no=tpl.order_no,
    status=0,
    max_duration=tpl.max_duration,
    create_by=:uid
  );
  mapTplToInst[tpl.key] = LAST_INSERT_ID();

-- D. 第2遍：用模板节点ID映射回填 prev/next 为实例ID数组
for each tpl in tplNodes:
  instId   := mapTplToInst[tpl.key];
  prevInst := [ mapTplToInst[x] for x in (tpl.prev or []) ];
  nextInst := [ mapTplToInst[x] for x in (tpl.next or []) ];
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

-- F. 生成工作项并激活起点
for each tpl in tplNodes:
  instId := mapTplToInst[tpl.key];
  -- 在任务创建时为所有节点/所有用户预生成工作项，非起点=0候选未到达，起点=1待办
  roleIds := tpl.roleIds or [];
  users := SELECT DISTINCT ur.user_id FROM sys_user_role ur
           WHERE JSON_CONTAINS(JSON_ARRAY(roleIds...), CAST(ur.role_id AS JSON), '$');
  phase := (tpl.prev is empty) ? 1 : 0;  -- 1=待办, 0=候选未到达
  for u in users:
    INSERT INTO tc_task_work_item(task_id, node_inst_id, assignee_id, phase_status, create_by)
    VALUES (:taskId, instId, u, phase, :uid);
  if tpl.prev is empty:
    UPDATE tc_task_node_inst SET status=1, started_at=NOW()
    WHERE id=instId AND status=0 AND del_flag=0;

COMMIT;
```

备注：handler\_role\_ids 为办理角色的快照，后续定义域变化不影响已生成任务。

---

### 2.2 任务列表

#### 2.2.1 Tab & 访问控制

Tab 值：
- **all** — 所有任务，仅管理员可用
- **startedByMe** — 发起的任务（创建人为我）
- **todo** — 待办任务（我的办理项及参与任务，phase_status∈(0,1)）
- **handled** — 已处理任务（我本人或我所属角色组有人处理过，phase_status∈(2,4,5)）

管理员使用非 all 的 tab 将返回 `TASKMANAGE_ADMIN_ONLY_ALL`；非管理员请求 all 将返回 `TASKMANAGE_ONLY_ADMIN_ALL`。

#### 2.2.2 统一筛选参数（全部可选）

| 参数名        | 类型     | 必填 | 说明                                   |
|---------------|----------|------|--------------------------------------|
| tab           | string   | 是   | 枚举：all / startedByMe / todo / handled |
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
  "satellites": [{"group":"三体星座","satIds":["1","2"]}],
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

说明：统一入口；通过 tab 区分四种列表；
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
  AND w.phase_status IN (0,1)
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
  AND w.phase_status IN (0,1)
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom );

```
**D) tab = handled（我或我组里的人处理过）**

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
  AND wi.phase_status  IN (2,4,5)
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
  AND wi.phase_status  IN (2,4,5)
  AND w.assignee_id = :userId
  AND ( :q IS NULL OR (t.task_name LIKE CONCAT('%', :q, '%') OR t.task_code LIKE CONCAT('%', :q, '%')) )
  AND ( :status IS NULL OR t.status = :status )
  AND ( :templateId IS NULL OR t.template_id = :templateId )
  AND ( :startTimeFrom IS NULL OR t.create_time >= :startTimeFrom );

```
注：上面 SELECT_FIELDS / WHERE_CONDITIONS / curN_subquery 为示意占位，实际写 SQL 时请替换成完整片段。


### 2.3 任务节点处理（办理与异常）

任务节点处理是任务管理的核心环节。每个节点包含若干操作选项（上传、选择计划、填写文本等），其中**至少有一个决策选项**。用户选择决策选项（执行 / 不执行）即视为对该节点的最终处理。

* **执行决策**：节点完成，推进工作流到后继节点。
* **不执行决策**：触发异常结束，整个任务进入异常状态。

---

#### 2.3.1 正常办理流程（执行决策）

当用户选择执行时，整体事务流程（**仅更新既有工作项，不再新增**）：

```sql
BEGIN;

-- A. 记录一次操作（动作、附件、文本、决策等）
INSERT INTO tc_task_node_action_record(task_id,node_inst_id,action_type,action_payload,create_by)
VALUES (:taskId,:nodeInstId,:type,:payloadJson,:uid);

-- B. 完成当前节点（行锁 + 幂等控制）
UPDATE tc_task_node_inst
SET status=2, completed_at=NOW(), completed_by=:uid, update_by=:uid
WHERE id=:nodeInstId AND task_id=:taskId AND status=1 AND del_flag=0;

-- C. 更新当前节点的工作项状态
UPDATE tc_task_work_item
SET phase_status=2, update_by=:uid
WHERE task_id=:taskId AND node_inst_id=:nodeInstId AND assignee_id=:uid;

UPDATE tc_task_work_item
SET phase_status=4, update_by=:uid
WHERE task_id=:taskId AND node_inst_id=:nodeInstId AND assignee_id<>:uid AND phase_status IN (0,1);

-- D. 推进后继节点（只做到达计数与激活；激活后仅“更新”既有工作项为待办）
nextIds := SELECT next_node_ids FROM tc_task_node_inst WHERE id=:nodeInstId FOR UPDATE;
for each nextId in nextIds:
  UPDATE tc_task_node_inst
  SET arrived_count = arrived_count + 1, update_by=:uid
  WHERE id=nextId AND del_flag=0;

  -- 满足所有前驱到达，激活节点
  UPDATE tc_task_node_inst
  SET status=1, started_at=NOW(), update_by=:uid
  WHERE id=nextId AND status=0 AND del_flag=0
    AND arrived_count >= JSON_LENGTH(prev_node_ids);

  -- 将该节点下所有预生成的工作项从 0(候选未到达) 更新为 1(待办)
  UPDATE tc_task_work_item
  SET phase_status=1, update_by=:uid
  WHERE task_id=:taskId AND node_inst_id=nextId AND del_flag=0 AND phase_status=0;

-- E. 任务完结判断（无任何 0/1 节点）
IF (SELECT COUNT(*) FROM tc_task_node_inst WHERE task_id=:taskId AND del_flag=0 AND status IN (0,1))=0 THEN
  UPDATE tc_task SET status=1, update_by=:uid WHERE id=:taskId AND del_flag=0;
END IF;

COMMIT;
```

---

#### 2.3.2 异常办理流程（不执行决策）

当用户选择“不执行”时，任务进入异常结束。**将原 B（仅当前节点异常）与 D（其他未完成节点异常）合并为单条语句**：

```sql
BEGIN;

-- A. 记录一次决策操作
INSERT INTO tc_task_node_action_record(task_id,node_inst_id,action_type,action_payload,create_by)
VALUES (:taskId,:nodeInstId,2,:payloadJson,:uid);

-- B&D 合并：将“当前节点 + 所有未完成节点”一并置为异常结束（不影响已完成节点）
UPDATE tc_task_node_inst
SET status=4, update_by=:uid
WHERE task_id=:taskId AND del_flag=0 AND status IN (0,1);

-- C. 任务标记为异常结束
UPDATE tc_task
SET status=2, update_by=:uid
WHERE id=:taskId AND del_flag=0;

-- D. 统一更新所有预生成工作项为异常结束
UPDATE tc_task_work_item
SET phase_status=5, update_by=:uid
WHERE task_id=:taskId AND del_flag=0 AND phase_status IN (0,1);

COMMIT;
```

---

#### 2.3.3 权限与校验

1. 节点必须 `status=1`（处理中）。
2. 操作人必须属于节点实例 `handler_role_ids` 对应角色：

   ```sql
   EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id=:uid AND JSON_CONTAINS(ni.handler_role_ids, CAST(ur.role_id AS JSON), '$'))
   ```
3. 幂等控制：完成节点时通过 `WHERE status=1` 避免重复。
4. 所有更新必须带 `update_by=:uid`，保证审计。

---

#### 2.3.4 状态总结

* **任务状态 `tc_task.status`**：0运行中 / 1结束 / 2异常结束 / 3取消
* **节点状态 `tc_task_node_inst.status`**：0待处理 / 1处理中 / 2已完成 / 3已取消 / 4异常结束
* **工作项状态 `tc_task_work_item.phase_status`**：

  * 0候选未到达
  * 1待办
  * 2我已处理
  * 3任务取消撤销
  * 4他人已完成失效
  * 5异常结束

---

通过以上机制，节点处理与异常中断统一到一个 **决策驱动的工作流处理引擎** 中，实现了节点、任务、工作项的完整生命周期管理。
 

### 2.4 编辑任务

**入参（JSON）**

taskId\*, taskName\*, taskRequirement,
needImaging(0/1), imagingArea(JSON|null), resultDisplayNeeded(0/1)

**事务流程（伪代码）**

```sql
BEGIN;

-- A. 权限 & 状态校验（服务层）
-- 仅发起人或管理员；任务 status=0 且无节点已完成

-- B. 更新任务主表（根据成像需求同步成像区域）
IF :needImaging = 0 THEN
  UPDATE tc_task
  SET task_name=:taskName, task_requirement=:taskRequirement,
      need_imaging=0, imaging_area=NULL,
      result_display_needed=:display,
      update_by=:uid, update_time=NOW()
  WHERE id=:taskId AND del_flag=0;
ELSE
  UPDATE tc_task
  SET task_name=:taskName, task_requirement=:taskRequirement,
      need_imaging=1, imaging_area=:imagingArea,
      result_display_needed=:display,
      update_by=:uid, update_time=NOW()
  WHERE id=:taskId AND del_flag=0;
END IF;

-- 若 :needImaging = 1 且 imagingArea 为空，服务层返回 TASKMANAGE_IMAGING_AREA_REQUIRED

-- C. result_display_needed 变化时增删“查看影像结果”节点及工作项
IF oldDisplay = 0 AND :display = 1 THEN
  endIds := SELECT id FROM tc_task_node_inst
            WHERE task_id=:taskId AND del_flag=0 AND JSON_LENGTH(next_node_ids)=0;
  -- 插入并连接查看节点（参见 §2.1E），并为其办理角色预生成 phase_status=0 的 tc_task_work_item
ELSEIF oldDisplay = 1 AND :display = 0 THEN
  -- 删除查看节点并清理末端指向，同时逻辑删除该节点的 tc_task_work_item
END IF;

COMMIT;
```

---

### 2.5 附件负荷示例

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

### 2.6 取消任务（只要“尚无人完成任何节点”）

允许起点在创建时即激活为“处理中(1)”（形成待办并开始计时）。
取消条件：任务 status=0 且不存在任何节点 status=2，且只有发起人或管理员可以执行取消。

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
  SET status=3, update_by=:userId, update_time=NOW()
  WHERE id=:taskId AND del_flag=0;

  UPDATE tc_task_node_inst
  SET status=3, update_by=:userId, update_time=NOW()
  WHERE task_id=:taskId AND del_flag=0 AND status IN (0,1);

  UPDATE tc_task_work_item
  SET phase_status=3, update_by=:userId, update_time=NOW()
  WHERE task_id=:taskId AND del_flag=0 AND phase_status IN (0,1);
END IF;

COMMIT;
```

---

 

### 2.8 超时提醒（定时任务）

目标：status=1 且 max\_duration 不空
判定：NOW() > started\_at + INTERVAL max\_duration MINUTE
触发：写消息/事件或直接推送；可附带 task\_id / node\_inst\_id / assignees（由实例 handler\_role\_ids → sys\_user\_role 解析）

---

## 3. 接口（REST）

### 3.1 任务

创建任务 `POST /task/create`
Body(JSON)：

```
taskName*, taskRequirement*, templateId*,
needImaging(0|1), imagingArea, resultDisplayNeeded(0|1),
satellites(JSON，如 [{"group":"阿联酋星座","satIds":["4","5"]}]), remoteCmds(List<RemoteCmdExportVO>), orbitPlans(List<OrbitPlanExportVO>)
```

Resp：`{ id }`（needImaging=1 时 imagingArea 必填）

取消任务 `POST /task/cancel?taskId=...`
Resp：`{ success: true }`（仅发起人或管理员且尚无人完成节点时允许；不满足条件报 TASKMANAGE\_NO\_PERMISSION 或 TASKMANAGE\_CANNOT\_CANCEL）

编辑任务 `POST /task/edit`
Body(JSON)：

```
taskId*, taskName, taskRequirement,
needImaging(0|1), imagingArea, resultDisplayNeeded(0|1)
```

Resp：`{ success: true }`（仅发起人或管理员且首节点未处理时允许；若 needImaging=0 将置空 imagingArea；若 resultDisplayNeeded 状态改变，将同步增删“查看影像结果”节点实例及工作项）

异常结束任务 `POST /task/abort?taskId=...`
Resp：`{ success: true }`

任务详情 `GET /task/detail?taskId=...`
Resp：任务主信息 + 当前激活节点ID集合（可选）

查询遥控指令单 `GET /task/remoteCmds?taskId=...`
Resp：`[RemoteCmdExportVO]`

查询轨道计划 `GET /task/orbitPlans?taskId=...`
Resp：`[OrbitPlanExportVO]`

列表 `GET /task/list?tab=&page=&pageSize=`

说明：统一接口，通过 `tab` 参数区分四种列表。管理员仅允许 `tab=all`，请求其他值返回 `TASKMANAGE_ADMIN_ONLY_ALL`；非管理员请求 `tab=all` 返回 `TASKMANAGE_ONLY_ADMIN_ALL`。

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

* **501 PARA_ERROR**：参数错误
* **131000 USERID_IS_NULL**：用户ID为空
* **200002 TASK_IS_NOT_EXISTS**：待办任务不存在
* **210001 TASKMANAGE_ADMIN_ONLY_ALL**：管理员只能查看所有任务
* **210002 TASKMANAGE_ONLY_ADMIN_ALL**：只有管理员可以查看所有任务
* **210003 TASKMANAGE_CANNOT_CANCEL**：已有节点完成，不能取消
* **210004 TASKMANAGE_NO_PERMISSION**：只有发起人或管理员可以编辑或取消任务
* **210005 TASKMANAGE_CANNOT_EDIT**：已有节点处理，不能编辑
* **210006 TASKMANAGE_IMAGING_AREA_REQUIRED**：成像区域不能为空
* **200004 INSTANCE_IS_NOT_EXISTS**：实例对象不存在
* **122000 STATUS_INVALID**：传入状态不合法
* **510 SC_JEECG_NO_AUTHZ**：访问权限认证未通过

**校验点：**

* 创建：templateId 有效；模板节点非空；needImaging=1 时 imagingArea 必填。
* 列表：管理员只能查询 `tab=all`，非管理员禁止 `tab=all`。
* 提交：节点必须处理中且提交者属于实例 handler_role_ids。
* 取消/编辑：任务 status=0 且无节点完成，且仅发起人或管理员可操作。

---

## 6. 并发与幂等

* 节点完成 `UPDATE ... WHERE status=1` 控制一次性；
* 后继激活使用 `arrived_count + status=0 + arrived_count >= JSON_LENGTH(prev_node_ids)`，可重放幂等；
* 办理（提交→完成→推进后继→完结判定）放单事务中。

---

## 7. 任务模板中的工作流信息，采用logicflow（参考https://site.logic-flow.cn/）的数据结构，该字段以json存在tc_todo_template的template_attr字段。
示例数据：
{
  "nodes": [
    {
      "id": 3,
      "type": "rect",
      "x": 625,
      "y": 84.66665649414062,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 3,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-11 14:50:24",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "确认遥测状态3",
          "description": "工作人员确认遥测状态3",
          "expectedDuration": 5,
          "timeoutRemind": 10,
          "status": 1,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            },
            {
              "roleId": "2",
              "roleName": "总体组组员"
            }
          ],
          "actions": [
            {
              "type": 0,
              "name": "上传操作3",
              "config": null
            },
            {
              "type": 2,
              "name": "决策操作3",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 625,
        "y": 84.66665649414062,
        "value": "确认遥测状态3"
      }
    },
    {
      "id": 1,
      "type": "rect",
      "x": 129,
      "y": 85.66665649414062,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 1,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-08 16:54:16",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "提交任务需求",
          "description": "用户提交任务需求信息",
          "expectedDuration": 15,
          "timeoutRemind": 10,
          "status": 1,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            }
          ],
          "actions": [
            {
              "type": 0,
              "name": "上传合同",
              "config": null
            },
            {
              "type": 2,
              "name": "合同审批",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 129,
        "y": 85.66665649414062,
        "value": "提交任务需求"
      }
    },
    {
      "id": 5,
      "type": "rect",
      "x": 381,
      "y": 276.6666564941406,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 5,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-11 15:17:38",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "确认遥测状态5",
          "description": "工作人员确认遥测状态6",
          "expectedDuration": 5,
          "timeoutRemind": 5,
          "status": 1,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            },
            {
              "roleId": "3",
              "roleName": "测运控组长"
            }
          ],
          "actions": [
            {
              "type": 0,
              "name": "上传操作5",
              "config": null
            },
            {
              "type": 2,
              "name": "决策操作5",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 381,
        "y": 276.6666564941406,
        "value": "确认遥测状态5"
      }
    },
    {
      "id": 7,
      "type": "rect",
      "x": 373,
      "y": 84.66665649414062,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 7,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-19 10:43:34",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "方案选择",
          "description": "选择圈次计划",
          "expectedDuration": 20,
          "timeoutRemind": 10,
          "status": 0,
          "roles": [
            {
              "roleId": "3",
              "roleName": "测运控组长"
            }
          ],
          "actions": [
            {
              "type": 1,
              "name": "选择圈次计划",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 373,
        "y": 84.66665649414062,
        "value": "方案选择"
      }
    },
    {
      "id": 9,
      "type": "rect",
      "x": 625.8333129882812,
      "y": 282.6666564941406,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 9,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-21 13:45:51",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "确认遥测状态4",
          "description": "工作人员确认遥测状态4",
          "expectedDuration": 4,
          "timeoutRemind": 4,
          "status": 0,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            },
            {
              "roleId": "2",
              "roleName": "总体组组员"
            }
          ],
          "actions": [
            {
              "type": 0,
              "name": "上传操作4",
              "config": null
            },
            {
              "type": 2,
              "name": "决策操作4",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 625.8333129882812,
        "y": 282.6666564941406,
        "value": "确认遥测状态4"
      }
    },
    {
      "id": 10,
      "type": "rect",
      "x": 607.8333129882812,
      "y": 509.6666564941406,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 10,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-26 15:37:03",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-26 15:37:03",
          "name": "确认遥测状态7",
          "description": "工作人员确认遥测状态7",
          "expectedDuration": 4,
          "timeoutRemind": 4,
          "status": 1,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            },
            {
              "roleId": "2",
              "roleName": "总体组组员"
            }
          ],
          "actions": [
            {
              "type": 0,
              "name": "上传操作7",
              "config": null
            },
            {
              "type": 2,
              "name": "决策操作7",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 607.8333129882812,
        "y": 509.6666564941406,
        "value": "确认遥测状态7"
      }
    },
    {
      "id": "a8b85181-183b-4b30-aef1-5ed37811ed00",
      "type": "rect",
      "x": 161.83331298828125,
      "y": 261.6666564941406,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 7,
          "creatorId": "1001",
          "creatorName": "张三",
          "createTime": "2025-08-19 10:43:34",
          "updaterId": "1001",
          "updaterName": "张三",
          "updateTime": "2025-08-22 17:58:37",
          "name": "方案选择",
          "description": "选择圈次计划",
          "expectedDuration": 20,
          "timeoutRemind": 10,
          "status": 0,
          "roles": [
            {
              "roleId": "3",
              "roleName": "测运控组长"
            }
          ],
          "actions": [
            {
              "type": 1,
              "name": "选择圈次计划",
              "config": null
            }
          ]
        }
      },
      "text": {
        "x": 161.83331298828125,
        "y": 261.6666564941406,
        "value": "方案选择"
      }
    },
    {
      "id": 11,
      "type": "rect",
      "x": 236.83331298828125,
      "y": 474.6666564941406,
      "properties": {
        "width": 160,
        "height": 80,
        "nodeConfigParams": {
          "id": 11,
          "creatorId": null,
          "creatorName": null,
          "createTime": "2025-08-26 16:07:15",
          "updaterId": null,
          "updaterName": null,
          "updateTime": "2025-08-26 16:15:38",
          "name": "4",
          "description": "4",
          "expectedDuration": 44,
          "timeoutRemind": 1,
          "status": 0,
          "roles": [
            {
              "roleId": "1",
              "roleName": "总体组组长"
            }
          ],
          "actions": []
        },
        "name": "结束",
        "description": ""
      },
      "text": {
        "x": 236.83331298828125,
        "y": 474.6666564941406,
        "value": "结束"
      }
    }
  ],
  "edges": [
    {
      "id": "bf31608b-7e8c-4b1a-9755-0cbbdd010115",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 1,
      "targetNodeId": 7,
      "sourceAnchorId": "1_1",
      "targetAnchorId": "7_3",
      "startPoint": {
        "x": 209,
        "y": 85.66665649414062
      },
      "endPoint": {
        "x": 293,
        "y": 84.66665649414062
      },
      "pointsList": [
        {
          "x": 209,
          "y": 85.66665649414062
        },
        {
          "x": 251,
          "y": 85.66665649414062
        },
        {
          "x": 251,
          "y": 84.66665649414062
        },
        {
          "x": 293,
          "y": 84.66665649414062
        }
      ]
    },
    {
      "id": "bf055a64-e395-4f37-8749-4db72111b4a9",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 7,
      "targetNodeId": 3,
      "sourceAnchorId": "7_1",
      "targetAnchorId": "3_3",
      "startPoint": {
        "x": 453,
        "y": 84.66665649414062
      },
      "endPoint": {
        "x": 545,
        "y": 84.66665649414062
      },
      "pointsList": [
        {
          "x": 453,
          "y": 84.66665649414062
        },
        {
          "x": 545,
          "y": 84.66665649414062
        }
      ]
    },
    {
      "id": "3aa36153-9e70-4040-b2c1-8963b5523bd7",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 3,
      "targetNodeId": 9,
      "sourceAnchorId": "3_2",
      "targetAnchorId": "9_0",
      "startPoint": {
        "x": 625,
        "y": 124.66665649414062
      },
      "endPoint": {
        "x": 625.8333129882812,
        "y": 242.66665649414062
      },
      "pointsList": [
        {
          "x": 625,
          "y": 124.66665649414062
        },
        {
          "x": 625,
          "y": 183.66665649414062
        },
        {
          "x": 625.8333129882812,
          "y": 183.66665649414062
        },
        {
          "x": 625.8333129882812,
          "y": 242.66665649414062
        }
      ]
    },
    {
      "id": "c11a53d4-4fab-401a-abad-9fdec56f57cc",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 3,
      "targetNodeId": 5,
      "sourceAnchorId": "3_2",
      "targetAnchorId": "5_0",
      "startPoint": {
        "x": 625,
        "y": 124.66665649414062
      },
      "endPoint": {
        "x": 381,
        "y": 236.66665649414062
      },
      "pointsList": [
        {
          "x": 625,
          "y": 124.66665649414062
        },
        {
          "x": 625,
          "y": 206.66665649414062
        },
        {
          "x": 381,
          "y": 206.66665649414062
        },
        {
          "x": 381,
          "y": 236.66665649414062
        }
      ]
    },
    {
      "id": "3fcdcb48-b79c-4bd9-bdf7-876430ed97a5",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 5,
      "targetNodeId": "a8b85181-183b-4b30-aef1-5ed37811ed00",
      "sourceAnchorId": "5_3",
      "targetAnchorId": "a8b85181-183b-4b30-aef1-5ed37811ed00_1",
      "startPoint": {
        "x": 301,
        "y": 276.6666564941406
      },
      "endPoint": {
        "x": 241.83331298828125,
        "y": 261.6666564941406
      },
      "pointsList": [
        {
          "x": 301,
          "y": 276.6666564941406
        },
        {
          "x": 271,
          "y": 276.6666564941406
        },
        {
          "x": 271,
          "y": 269.1666564941406
        },
        {
          "x": 271.83331298828125,
          "y": 269.1666564941406
        },
        {
          "x": 271.83331298828125,
          "y": 261.6666564941406
        },
        {
          "x": 241.83331298828125,
          "y": 261.6666564941406
        }
      ]
    },
    {
      "id": "8f0c5f1b-ffcf-4b1c-9010-25fdbfef8dd7",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 9,
      "targetNodeId": 10,
      "sourceAnchorId": "9_2",
      "targetAnchorId": "10_0",
      "startPoint": {
        "x": 625.8333129882812,
        "y": 322.6666564941406
      },
      "endPoint": {
        "x": 607.8333129882812,
        "y": 469.6666564941406
      },
      "pointsList": [
        {
          "x": 625.8333129882812,
          "y": 322.6666564941406
        },
        {
          "x": 625.8333129882812,
          "y": 396.1666564941406
        },
        {
          "x": 607.8333129882812,
          "y": 396.1666564941406
        },
        {
          "x": 607.8333129882812,
          "y": 469.6666564941406
        }
      ]
    },
    {
      "id": "4906c2c4-66fe-42fa-a4a4-c5b4e2df75e3",
      "type": "polyline",
      "properties": {},
      "sourceNodeId": 10,
      "targetNodeId": 11,
      "sourceAnchorId": "10_3",
      "targetAnchorId": "11_1",
      "startPoint": {
        "x": 527.8333129882812,
        "y": 509.6666564941406
      },
      "endPoint": {
        "x": 316.83331298828125,
        "y": 474.6666564941406
      },
      "pointsList": [
        {
          "x": 527.8333129882812,
          "y": 509.6666564941406
        },
        {
          "x": 422.33331298828125,
          "y": 509.6666564941406
        },
        {
          "x": 422.33331298828125,
          "y": 474.6666564941406
        },
        {
          "x": 316.83331298828125,
          "y": 474.6666564941406
        }
      ]
    }
  ]
}

