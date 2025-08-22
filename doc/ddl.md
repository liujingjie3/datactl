# 任务管理模块——数据库方案


## 一、数据库表设计（DDL）

### A. 系统与模板域

#### 1) 用户表 `sys_user`

```sql
CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `username` varchar(100) DEFAULT NULL COMMENT '登录账号',
  `realname` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(45) DEFAULT NULL COMMENT 'md5密码盐',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别(0未知,1男,2女)',
  `email` varchar(45) DEFAULT NULL COMMENT '电子邮件',
  `phone` varchar(45) DEFAULT NULL COMMENT '电话',
  `org_code` varchar(64) DEFAULT NULL COMMENT '机构编码',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态(1正常,2冻结)',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态(0正常,1已删除)',
  `third_id` varchar(100) DEFAULT NULL COMMENT '第三方登录唯一标识',
  `third_type` varchar(100) DEFAULT NULL COMMENT '第三方类型',
  `activiti_sync` tinyint(1) DEFAULT NULL COMMENT '同步工作流引擎(1同步,0不同步)',
  `work_no` varchar(100) DEFAULT NULL COMMENT '工号',
  `post` varchar(100) DEFAULT NULL COMMENT '职务',
  `telephone` varchar(45) DEFAULT NULL COMMENT '座机号',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `user_identity` tinyint(1) DEFAULT NULL COMMENT '身份(1普通成员,2上级)',
  `depart_ids` varchar(1000) DEFAULT NULL COMMENT '负责部门',
  `client_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `login_tenant_id` int DEFAULT NULL COMMENT '上次登录租户ID',
  `bpm_status` varchar(2) DEFAULT NULL COMMENT '流程状态',
  `user_type` int DEFAULT NULL COMMENT '用户类型(0公众,1科研,2业务)',
  `nation` varchar(45) DEFAULT NULL COMMENT '国家',
  `province` varchar(45) DEFAULT NULL COMMENT '省份',
  `application_area` varchar(45) DEFAULT NULL COMMENT '应用领域',
  `description` varchar(45) DEFAULT NULL COMMENT '备注',
  `social_credit_code` varchar(45) DEFAULT NULL COMMENT '单位社会信用代码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户表';
```

#### 2) 角色表 `sys_role`

```sql
CREATE TABLE `sys_role` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `role_name` varchar(200) DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(100) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` int DEFAULT 0 COMMENT '租户ID',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态(1正常,2作废)',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态(0正常,1已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='角色表';
```

#### 3) 用户角色表 `sys_user_role`

```sql
CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户id',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色id',
  `tenant_id` int DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户角色表';
```

#### 4) 模板主表 `tc_todo_template`

```sql
CREATE TABLE `tc_todo_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(128) NOT NULL COMMENT '模板编码',
  `template_id` varchar(128) DEFAULT NULL COMMENT '模板id',
  `template_type` varchar(64) NOT NULL COMMENT '模板类型',
  `template_name` varchar(128) NOT NULL COMMENT '模板名称',
  `flag` tinyint NOT NULL COMMENT '状态标识(0待上线,1上线)',
  `template_attr` text COMMENT '模板属性json',
  `corp_id` varchar(255) DEFAULT NULL COMMENT '客户端标识',
  `agent_id` varchar(255) NOT NULL COMMENT '客户端应用标识',
  `ext` text COMMENT '扩展字段',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标志位',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` int NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实例模板信息表';
```

---

### B. 任务模板域

#### 5) 模板节点定义表 `tc_task_template_node`

```sql
CREATE TABLE `tc_task_template_node` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板节点记录ID',
  `template_id` varchar(128) NOT NULL COMMENT '任务模板ID',
  `node_id` BIGINT NOT NULL COMMENT '节点ID',
  `order_no` INT COMMENT '层级序号',
  `prev_keys` JSON COMMENT '前驱节点键数组',
  `next_keys` JSON COMMENT '后继节点键数组',
  `max_duration` INT COMMENT '最大时长（分钟）',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (`prev_keys` IS NULL OR JSON_VALID(`prev_keys`)),
  CHECK (`next_keys` IS NULL OR JSON_VALID(`next_keys`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板-节点定义表';
```

---

### C. 任务域

#### 6) 任务主表 `tc_task`

```sql
CREATE TABLE `tc_task` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
  `task_code` VARCHAR(50) NOT NULL COMMENT '任务编码',
  `task_name` VARCHAR(50) NOT NULL COMMENT '任务名称',
  `task_requirement` VARCHAR(255) COMMENT '任务需求',
  `template_id` varchar(128) NOT NULL COMMENT '任务模板ID',
  `need_imaging` TINYINT DEFAULT 0 COMMENT '是否需要成像(0否,1是)',
  `imaging_area` JSON NULL COMMENT '成像区域JSON',
  `result_display_needed` TINYINT DEFAULT 0 COMMENT '结果是否展示(0否,1是)',
  `satellites` JSON COMMENT '执行卫星JSON',
  `remote_cmds` JSON COMMENT '遥感指令JSON',
  `orbit_plans` JSON COMMENT '轨道计划JSON数组',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '任务状态(0运行中,1结束,2异常结束,3取消)',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (`imaging_area` IS NULL OR JSON_VALID(`imaging_area`)),
  CHECK (`satellites` IS NULL OR JSON_VALID(`satellites`)),
  CHECK (`remote_cmds` IS NULL OR JSON_VALID(`remote_cmds`)),
  CHECK (`orbit_plans` IS NULL OR JSON_VALID(`orbit_plans`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务主表';
```

#### 7) 任务节点实例表 `tc_task_node_inst`

```sql
CREATE TABLE `tc_task_node_inst` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '节点实例ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `template_id` varchar(128) NOT NULL COMMENT '任务模板ID',
  `node_id` BIGINT NOT NULL COMMENT '节点ID',
  `prev_node_ids` JSON COMMENT '前驱节点实例ID数组',
  `next_node_ids` JSON COMMENT '后继节点实例ID数组',
  `arrived_count` INT NOT NULL DEFAULT 0 COMMENT '已到达前驱计数',
  `handler_role_ids` JSON COMMENT '处理角色ID数组',
  `order_no` INT COMMENT '层级序号',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '节点状态(0待处理,1处理中,2已完成,3已取消,4异常结束)',
  `started_at` DATETIME COMMENT '开始时间',
  `completed_at` DATETIME COMMENT '完成时间',
  `max_duration` INT COMMENT '最大时长（分钟）',
  `completed_by` varchar(32) DEFAULT NULL COMMENT '最终完成该节点的用户ID',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (`prev_node_ids` IS NULL OR JSON_VALID(`prev_node_ids`)),
  CHECK (`next_node_ids` IS NULL OR JSON_VALID(`next_node_ids`)),
  CHECK (`handler_role_ids` IS NULL OR JSON_VALID(`handler_role_ids`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务节点实例';
```

#### 8) 任务工作项表 `tc_task_work_item`

```sql
CREATE TABLE `tc_task_work_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `node_inst_id` BIGINT NOT NULL COMMENT '节点实例ID',
  `assignee_id` varchar(32) NOT NULL COMMENT '分配用户ID',
  `phase_status` TINYINT NOT NULL DEFAULT 0 COMMENT '阶段(0候选未到达,1待办,2我已处理,3任务取消撤销,4他人已完成失效,5异常结束)',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务工作项';
```

#### 9) 节点操作记录表 `tc_task_node_action_record`

```sql
CREATE TABLE `tc_task_node_action_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `node_inst_id` BIGINT NOT NULL COMMENT '节点实例ID',
  `action_type` TINYINT NOT NULL COMMENT '操作类型(0上传,1选择计划,2决策,3文本等)',
  `action_payload` JSON COMMENT '提交内容JSON',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (`action_payload` IS NULL OR JSON_VALID(`action_payload`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点操作记录';
```

---

### D. 节点管理域

#### 10) 节点信息表 `tc_node_info`

```sql
CREATE TABLE `tc_node_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '节点ID',
  `name` VARCHAR(100) NOT NULL COMMENT '节点名称',
  `description` VARCHAR(255) COMMENT '节点描述',
  `expected_duration` INT COMMENT '预计处理时长（分钟）',
  `timeout_remind` INT COMMENT '超时提醒频率（分钟）',
  `status` TINYINT DEFAULT 1 COMMENT '节点状态(1活跃,0禁用)',
  `actions` JSON NOT NULL COMMENT '操作控制项数组：[{type,name,config}]',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记(0正常,1删除)',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点信息表';
```

#### 11) 节点角色关联表 `tc_node_role_rel`

```sql
CREATE TABLE `tc_node_role_rel` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `node_id` BIGINT NOT NULL COMMENT '节点ID',
  `role_id` varchar(32) NOT NULL COMMENT '角色ID',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记(0正常,1删除)',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点与角色关联表（多对多）';
```
