# 通知子系统设计文档

## 0. 设计目标与范围

● 覆盖触发时机：
○ 任务创建完成 → 通知「工作流中所有节点（发起人除外）」
○ 当前节点完成 → 通知「下一节点负责人」
○ 进站前提醒（每圈次进站前 1 小时起、每 15 分钟一次直至进站）→ 通知「工作流中所有节点（含发起人）」
○ 任务完成/任务异常结束 → 通知「工作流中所有节点（含发起人）」
○ 超时提醒（节点预计时长/模板规则） → 通知「当前代办节点的责任人」
● 统一抽象消息渠道（OA/钉钉…），模板化文案，支持重试与去重（Outbox 模式），与主业务解耦。
● 支持幂等（dedup\_key）、失败重试、限流、审计与可观测。

## 1. 枚举与编码

为便于高效查询与压缩存储，biz\_type与 channel 使用数值枚举。

### 1.1 biz\_type（TINYINT）

| 值                                                                       | 含义             |
| ----------------------------------------------------------------------- | -------------- |
| 0                                                                       | TASK\_CREATED  |
| 1                                                                       | NODE\_DONE     |
| 2                                                                       | ORBIT\_REMIND  |
| 3                                                                       | TASK\_FINISHED |
| 4                                                                       | TASK\_ABNORMAL |
| 5                                                                       | NODE\_TIMEOUT  |
| 说明：biz\_id 的含义随 biz\_type 变化：0/3/4/2 使用 task\_id，1/5 使用 node\_inst\_id。 |                |

### 1.2 channel（TINYINT）

| 值 | 含义 |
| - | -- |
| 0 | OA |
| 1 | 钉钉 |

## 2. 数据模型（DDL + 索引）

### 2.1 推送任务表 tc\_notify\_job

存放“要发”的一条通知任务（收件人由模板展开后写入明细表）。

> 该模块为单租户设计，表结构中不包含 `tenant_id` 字段。

```sql
CREATE TABLE `tc_notify_job` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知任务ID',
  `biz_type` TINYINT NOT NULL COMMENT '0任务创建/1节点完成/2进站提醒/3任务完成/4任务异常/5节点超时',
  `biz_id` BIGINT NOT NULL COMMENT '业务主键（task_id 或 node_inst_id）',
  `dedup_key` VARCHAR(128) DEFAULT NULL COMMENT '幂等去重键（相同key不重复投递）',
  `payload` JSON NOT NULL COMMENT '业务上下文快照JSON（模板渲染所需变量）',
  `channel` TINYINT NOT NULL COMMENT '0OA / 1钉钉',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理,1成功,2失败,3取消,4异常结束',
  `retry_count` INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
  `next_run_time` DATETIME NOT NULL COMMENT '下一次尝试时间（用于重试或定时提醒）',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0正常，1删除',
  `create_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知任务';

-- 索引
CREATE INDEX `idx_job_status_time` ON `tc_notify_job`(`status`,`next_run_time`);
CREATE INDEX `idx_job_biz` ON `tc_notify_job`(`biz_type`,`biz_id`);
CREATE UNIQUE INDEX `uk_job_dedup_del` ON `tc_notify_job`(`dedup_key`, `del_flag`);
CREATE INDEX `idx_job_channel` ON `tc_notify_job`(`channel`);
```

### 2.2 推送收件人表 tc\_notify\_recipient

每个 job 展开后的具体收件人。

```sql
CREATE TABLE `tc_notify_recipient` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收件人记录ID',
  `job_id` BIGINT NOT NULL COMMENT 'tc_notify_job.id',
  `user_id` VARCHAR(32) NOT NULL COMMENT '接收人用户ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待发,1成功,2失败,3取消,4异常结束',
  `last_error` VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知收件人明细';

-- 索引
CREATE INDEX `idx_rec_job` ON `tc_notify_recipient`(`job_id`);
CREATE INDEX `idx_rec_status` ON `tc_notify_recipient`(`status`);
CREATE INDEX `idx_rec_user` ON `tc_notify_recipient`(`user_id`);
```

### 2.3 模板表 tc\_notify\_template

每种 biz\_type 的多渠道模板配置（可后台维护）。

```sql
CREATE TABLE `tc_notify_template` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `biz_type` TINYINT NOT NULL COMMENT '业务类型',
  `channel` TINYINT NOT NULL COMMENT '0OA / 1钉钉',
  `title_tpl` VARCHAR(200) NOT NULL COMMENT '标题模板，支持占位符',
  `content_tpl` TEXT NOT NULL COMMENT '正文模板（占位符如 ${taskName}）',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `del_flag` TINYINT DEFAULT 0,
  `create_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_biz_channel` (`biz_type`,`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知模板';
```

### 2.4 渠道配置表 tc\_notify\_channel\_config

存 webhook、token 等渠道配置。

```sql
CREATE TABLE `tc_notify_channel_config` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `channel` TINYINT NOT NULL COMMENT '0OA / 1钉钉',
  `config_json` JSON NOT NULL COMMENT '渠道配置，如钉钉机器人地址、签名key等',
  `enabled` TINYINT NOT NULL DEFAULT 1,
  `del_flag` TINYINT DEFAULT 0,
  `create_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_channel` (`channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道配置';
```

## 3. 触发点与入库（Outbox）

与主业务解耦：业务事务提交成功后 写入 tc\_notify\_job + tc\_notify\_recipient，由分发器异步发送。

### 3.1 任务创建完成（biz\_type=0）

● 触发：TaskService.createTask() 事务成功后。
● 受众：该任务下所有节点实例的办理人（handler\_role\_ids → sys\_user\_role 展开），排除发起人。
● 去重：dedup\_key = '0\_' || taskId || '\_' || channel。
● biz\_id=task\_id，next\_run\_time=NOW()。
● 例如，当taskId=1,OA触发，任务创建，则dedup\_key=0\_1\_0

### 3.2 当前节点完成（biz\_type=1）

● 触发：TaskService.submitAction() 节点由 1→2 成功后。
● 受众：所有“下一节点”实例的办理人。
● 去重：dedup\_key = '1\_' || taskId || '\_' || nodeInstId || '\_' || channel。
● biz\_id=node\_inst\_id，next\_run\_time=NOW()。

### 3.3 进站前提醒（biz\_type=2）

● 触发：定时规划器（见 §6.2）根据 tc\_task.orbit\_plans 切片生成多个时点。
● 受众：工作流全体（含发起人）。
● 去重：dedup\_key = '2\_' || taskId || '\_' || orbitNo || '\_' || remainMin || '\_' || channel。
● biz\_id=task\_id，next\_run\_time=slotTime。

### 3.4 任务完成/异常（biz\_type=3/4）

● 触发：任务 status 由 0→1 / 0→2。
● 受众：工作流全体（含发起人）。
● 去重：'3\_'||taskId||'\_'||channel / '4\_'||taskId||'\_'||channel。
● biz\_id=task\_id，next\_run\_time=NOW()。

### 3.5 超时提醒（biz\_type=5）

● 触发：节点实例被激活时由 `scheduleNodeTimeout` 入库（创建任务时为起始节点，节点办理完成后为已激活的下一节点）。
● 条件：节点配置了 `max_duration`，以节点开始办理时间（`started_at`）+ `max_duration` 作为首次提醒触发点。
● 受众：该节点当前办理人（以 `tc_task_work_item` 的待办记录定位）。
● 去重：dedup\_key = '5\_'||nodeInstId||'\_'||channel。
● biz\_id=node\_inst\_id，next\_run\_time=started\_at + max\_duration（若 started\_at 为空则按入库时间计算）。
● 重复提醒：payload 中若存在 `timeoutRemind`（分钟），分发器成功推送后会检查节点仍为办理中状态（`tc_task_node_inst.status=1`），继续将同一 Job 的 `next_run_time` 顺延 `timeoutRemind` 分钟，直至节点完成或取消。

## 4. 核心组件与接口

### 4.1 Facade（应用门面）

```java
public interface NotifyFacade {
  long onTaskCreated(long taskId, List<Integer> channels, String operator);
  long onNodeDone(long taskId, long nodeInstId, List<Integer> channels, String operator);
  long onTaskFinished(long taskId, List<Integer> channels, String operator);
  long onTaskAbnormal(long taskId, String reason, List<Integer> channels, String operator);
  // 进站提醒与超时提醒由调度器触发，Facade 暴露可选手动触发：
  List<Long> planOrbitReminds(long taskId, List<Integer> channels, String operator);
  List<Long> enqueueNodeTimeout(long nodeInstId, List<Integer> channels, String operator);
}
```

### 4.2 入库服务（Service）

```java
public interface NotifyService {
  long enqueue(byte bizType, long bizId, byte channel,
               JSONObject payload, List<String> userIds,
               String dedupKey, LocalDateTime nextRunTime,
               String operator);
}
行为：
1. 检查 dedup_key 是否存在 → 存在直接返回对应 jobId（幂等）。
2. 插入 tc_notify_job；批量插入 tc_notify_recipient（对 userIds 去重）。
```

### 4.3 收件人解析（RecipientResolver）

```java
public interface NotifyRecipientResolver {
  Set<String> resolveAllParticipants(long taskId, boolean includeCreator);
  Set<String> resolveNextNodeHandlers(long taskId, long currentNodeInstId);
  Set<String> resolveCurrentTodoUsers(long nodeInstId);
}
```

### 4.4 模板渲染（TemplateRenderer）

```java
public interface TemplateRenderer {
  RenderedMsg render(byte bizType, byte channel, JSONObject payload);
  class RenderedMsg { public String title; public String content; }
}
```

### 4.5 渠道驱动（ChannelDriver SPI）

```java
public interface ChannelDriver {
  byte channelCode(); // 0=OA,1=钉钉
  SendResult send(String userId, String title, String content, JSONObject payload);
  class SendResult { public boolean ok; public String error; }
}
```

提供 DingTalkRobotDriver 与 OAHttpDriver 两个实现，配置从 tc\_notify\_channel\_config 读取。

### 4.6 分发器（Dispatcher）

● 单机或集群部署，支持 FOR UPDATE SKIP LOCKED 抢占。
伪代码：

```sql
-- 拉取待执行任务
SELECT * FROM tc_notify_job
WHERE status=0 AND next_run_time <= NOW() AND del_flag=0
ORDER BY next_run_time ASC
LIMIT 200 FOR UPDATE SKIP LOCKED;
```

Java 流程：

1. 置发送中（可选）。
2. 加载模板并渲染标题/正文；查询 tc\_notify\_recipient 批量发送。
3. 全部成功 → status=1。若 biz\_type=NODE\_TIMEOUT 且 payload 中包含 timeoutRemind，则在标记成功前调用调度逻辑检查节点仍在办理中：
   ● 若仍在办理中 → job `next_run_time` = now + timeoutRemind，保持 status=0；
   ● 否则 → 直接标记成功。
   部分失败 → status=2，retry\_count++，回填 next\_run\_time = now + backoff(retry\_count)；超过上限 → 维持失败并记录 last\_error。

### 4.7 规划器/调度器（Scheduler）

● 进站提醒：由任务管理服务（`scheduleOrbitReminders`）在任务创建或轨道计划调整时解析 tc\_task.orbit\_plans，为每个圈次生成 T-60/T-45/T-30/T-15/T-0 的 tc\_notify\_job（若不存在 dedup\_key）。
● 节点超时：同一服务在节点激活时调用 `scheduleNodeTimeout` 写入初始提醒 Job，并在分发器中根据 timeoutRemind 自动顺延，取代额外的扫描器。
● NotifyDispatcher：消费待发任务并推送，同时负责 NodeTimeout 的重复提醒调度。

## 5. 模板与 payload 规范

### 5.1 模板示例（可后台维护）

● TASK\_CREATED / 钉钉：
○ Title：【\${taskName}】已发起
○ Content：【星地协同平台】\${creatorName} 于 \${createTime} 发起了【\${taskName}】，请及时关注代办任务

● NODE\_DONE / 钉钉：
○ Title：【\${taskName}】进入下一节点
○ Content：【星地协同平台】您好，您有一个\${taskName}任务需要马上处理\${nextNodeName}，剩余\${remainMin}分钟即将超时，请登录系统进行处理，感谢支持！

● ORBIT\_REMIND / 钉钉：
○ Title：【\${taskName}】进站提醒
○ Content：【星地协同平台】您好，任务\${taskName}执行卫星将于\${remainMin}分钟后进站，请提前开展工作准备，感谢支持！

● TASK\_FINISHED / OA：
○ Title：【\${taskName}】已完成
○ Content：【星地协同平台】您好，任务\${taskName}执行完成，如需查看详情请登录系统。

● TASK\_ABNORMAL / 钉钉：
○ Title：【\${taskName}】异常结束
○ Content：【星地协同平台】您好，任务\${taskName}执行异常结束，如需查看详情请登录系统。

● NODE\_TIMEOUT / 钉钉：
○ Title：【\${taskName}】节点超时
○ Content：【星地协同平台】您好，您参与的任务\${taskName}节点\${nodeName}已经超时\${maxDuration}分钟，请尽快登录系统进行处理，感谢支持！

### 5.2 示例 payload（由业务侧封装）

```json
{
  "taskId": 1001,
  "taskName": "应急成像-京津冀",
  "taskCode": "T2025-0001",
  "creatorName": "张三",
  "createTime": "2025-08-01 10:20",
  "nextNodeName": "资源确认",
  "nextNodeNames": "资源确认, 数据准备",
  "orbitId": "O-2025-0009",
  "orbitNo": 9,
  "inTime": "2025-08-02 14:00",
  "remainMin": 45,
  "finishTime": "2025-08-02 20:30",
  "reason": "B节点决策为不执行",
  "nodeInstId": 8882,
  "nodeName": "资源确认",
  "maxDuration": 60
}
```

## 6. 去重、重试与限流

● 去重（dedup\_key）：同一业务事件在同一渠道仅生成一次 Job。即使上游重复调用，也只返回已存在的 jobId。
● 重试：失败后指数退避（如 1m, 5m, 30m, 1h, 3h），上限 max\_retries=5。
● 限流：渠道驱动内部实现（例如钉钉机器人每分钟 N 条）；分发器按渠道分桶发送（可选）。
● 取消：当任务被取消或异常结束，如需停止未来计划的提醒，将对应 Job status=3（取消）或 status=4（异常结束），收件人明细状态与之对应。

## 7. 与任务管理的对接点（关键调用）

● TaskService.createTask() → NotifyFacade.onTaskCreated(taskId, channels, operator)
● TaskService.submitAction() 节点完成后 → NotifyFacade.onNodeDone(taskId, nodeInstId, channels, operator)
● 任务状态流转 → onTaskFinished / onTaskAbnormal
● 调度器：OrbitRemindPlanner、NodeTimeoutScanner、NotifyDispatcher

## 8. 代码骨架（示例）

```java
@Service
public class NotifyServiceImpl implements NotifyService {
  @Transactional
  public long enqueue(byte bizType, long bizId, byte channel,
                      JSONObject payload, List<String> userIds,
                      String dedupKey, LocalDateTime nextRunTime,
                      String operator) {
    Long jobId = notifyJobMapper.selectIdByDedupKey(dedupKey);
    if (jobId != null) return jobId;

    jobId = notifyJobMapper.insertJob(bizType, bizId, channel, payload, dedupKey, nextRunTime, operator);
    List<String> distinctUsers = userIds.stream().distinct().toList();
    notifyRecipientMapper.batchInsert(jobId, distinctUsers, operator);
    return jobId;
  }
}

@Component
public class NotifyDispatcher {
  @Scheduled(fixedDelay = 3000)
  public void dispatch() {
    List<Job> jobs = jobMapper.lockDueJobs(200); // SELECT ... FOR UPDATE SKIP LOCKED
    for (Job j : jobs) {
      RenderedMsg msg = renderer.render(j.bizType, j.channel, j.payload);
      List<Recipient> rs = recMapper.findByJobId(j.id);
      boolean allOk = true;
      for (Recipient r : rs) {
        SendResult sr = driver(j.channel).send(r.userId, msg.title, msg.content, j.payload);
        recMapper.updateStatus(r.id, sr.ok, sr.error);
        if (!sr.ok) allOk = false;
      }
      if (allOk) jobMapper.markSuccess(j.id);
      else jobMapper.scheduleRetry(j.id, calcNext(j.retryCount+1), j.retryCount+1);
    }
  }
}
```

## 9. 收件人解析策略

● 工作流全体：遍历该任务的 tc\_task\_node\_inst，汇总 handler\_role\_ids → sys\_user\_role 得到用户集合；可追加发起人 tc\_task.create\_by。
● 下一节点负责人：取当前节点实例的 next\_node\_ids 对应实例，按其 handler\_role\_ids 展开。
● 当前代办责任人：优先用 tc\_task\_work\_item 中 phase\_status=1 的 assignee\_id。
● 排除发起人：在任务创建通知中，从集合中剔除 create\_by。

## 10. 测试清单

● 任务创建 → 全体（不含发起人）收到通知。
● 节点完成 → 下一节点办理人收到通知（并行多后继）。
● 进站提醒 → 每圈次 5 个时点生成并按时发送；去重有效。
● 任务完成/异常 → 全体广播一次；重复触发不重复发送。
● 超时提醒 → 首次/重复策略正确，提醒人群准确。
● 渠道限流与重试 → 触发退避重试；最终失败可见。
● 并发幂等 → 重复调用 enqueue 不产生重复 Job（dedup\_key 有效）。
