# 节点管理模块（TcNodeController）

## 0. 模块概述

节点管理模块负责维护任务流转节点的基础信息，提供节点的统计、分页查询、创建、编辑、状态更新、删除及详情查询等能力。所有接口都挂载在 `TcNodeController`，服务实现位于 `TcNodeServiceImpl`。

---

## 1. 数据模型

### 1.1 节点定义 `tc_node_info`

| 字段 | 说明 |
| --- | --- |
| id | 主键，自增 |
| name | 节点名称 |
| description | 描述 |
| expected_duration | 预计处理时长（分钟） |
| timeout_remind | 超时提醒频率（分钟） |
| status | 节点状态：1=活跃，0=禁用 |
| actions | 操作控制项数组 JSON，如 `[{type,name,config}]` |
| del_flag | 逻辑删除标记 |
| create_by / create_time | 创建人及时间 |
| update_by / update_time | 更新人及时间 |

### 1.2 节点与角色关联 `tc_node_role_rel`

| 字段 | 说明 |
| --- | --- |
| id | 主键，自增 |
| node_id | 节点ID |
| role_id | 角色ID |
| del_flag、create_by、create_time、update_by、update_time | 公共审计字段 |

### 1.3 DTO 与辅助结构

* **NodeInfoDto**：用于请求和响应的节点信息载体，包含节点基础字段、角色列表、操作控制项数组及创建/更新人信息。
* **NodeRoleDto**：`{ roleId, roleName }`。
* **NodeActionDto**：`{ type, name, config }`，type 取值：0上传、1选择圈次计划、2决策、3文本填写。
* **NodeQueryDto**：列表查询参数，支持按 `status`、`name`、`roleId` 过滤，并分页（page/pageSize）。
* **NodeStatsVO**：节点统计数据载体，`{ activeCount, disabledCount, totalCount }`。

---

## 2. 核心流程

### 2.1 获取节点统计

`TcNodeService.getStats()` 统计活跃、禁用及总节点数。

### 2.2 分页查询节点

`TcNodeService.listNodes(queryDto)` 根据查询条件分页返回节点列表。结果中的角色名称、动作仅保留 `type` 与 `name`，并附带创建/更新人信息。

### 2.3 新建节点

`TcNodeService.createNode(dto)` 创建节点：
1. 写入 `tc_node_info`，绑定操作控制项。
2. 追加节点与角色关联 `tc_node_role_rel`。

### 2.4 编辑节点

`TcNodeService.updateNode(id, dto)` 更新节点基本信息及动作配置，同时对比新旧角色集合，发生变化时重建 `tc_node_role_rel` 关联。

### 2.5 更新节点状态

`TcNodeService.updateStatus(id, status)` 仅更新 `tc_node_info.status`。

### 2.6 删除节点

`TcNodeService.deleteNode(id)` 物理删除节点，并清理关联角色。

### 2.7 获取节点详情

`TcNodeService.getDetail(id)` 返回节点完整信息，包括动作配置、角色列表以及创建/更新人。

---

## 3. 接口一览

| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/tc/node/stats` | 获取节点统计数据 |
| GET | `/tc/node/list` | 查询节点列表，参数：`status?`、`name?`、`roleId?`、`page`、`pageSize` |
| POST | `/tc/node/create` | 新建节点，请求体为 `NodeInfoDto` |
| PUT | `/tc/node/update/{id}` | 编辑节点 |
| PATCH | `/tc/node/status/{id}?status=` | 更新节点状态 |
| DELETE | `/tc/node/delete/{id}` | 删除节点 |
| GET | `/tc/node/detail/{id}` | 获取节点详情 |

---

## 4. 异常与校验

* 创建、编辑需获取当前登录用户ID，缺失时抛出 `USERID_IS_NULL`。
* 角色关联在更新时进行新旧集合对比，避免不必要的数据库操作。

---

## 5. 扩展点

* `actions` 字段采用 JSON 结构，后续可扩展新的控制项类型。
* `expected_duration`、`timeout_remind` 等字段为后续节点超时提醒、统计提供数据基础。

