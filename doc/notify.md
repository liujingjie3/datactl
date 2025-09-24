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

> 表结构沿用系统通用的 `BasePo` 约定，包含基础审计字段（del_flag、create_by、update_by 等）。

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
  `external_msgid` VARCHAR(128) DEFAULT NULL COMMENT '第三方返回的消息ID',
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
  `external_tpl_id` VARCHAR(64) DEFAULT NULL COMMENT '第三方平台模板ID',
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


钉钉渠道建议的 `config_json` 结构：

```json
{
  "baseUrl": "http://10.102.3.2/MCConsumerApplication/consumer/mc/send",
  "agentId": "ZJLAB1045002",
  "corpId": "ZJLAB1045"
}
```

`baseUrl` 需要提供完整的发送地址；当前实现不再支持拆分配置（如 `scheme`/`address`/`path`），若三方地址调整请直接更新该字段。

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
● 重复提醒：payload 中若存在 `timeoutRemind`（分钟），分发器成功推送后会检查节点仍为办理中状态（`tc_task_node_inst.status=1`），继续将同一 Job 的 `next_run_time` 顺延 `timeoutRemind` 分钟，直至节点完成或取消；分发前还会通过 `enrichTimeoutPayload` 计算“已超时分钟数”，将 payload 中的 `maxDuration` 覆写为实际超时时长，并追加 `overtime` 字段供模板引用。

## 4. 核心组件与接口

### 4.1 NotifyService（通知入库服务）

```java
public interface NotifyService {
    long enqueue(byte bizType, long bizId, byte channel,
                 JSONObject payload, List<String> userIds,
                 String dedupKey, LocalDateTime nextRunTime,
                 String operator);

    void updateStatus(byte bizType, Collection<Long> bizIds,
                      byte status, Collection<Byte> expectedStatuses,
                      String operator);

    void deleteByBiz(byte bizType, Collection<Long> bizIds, String operator);
}
```

- `enqueue` 先按 `dedup_key` 查重（为空则跳过），命中直接返回已存在的 Job ID；否则写入 `tc_notify_job` 并为去重后的收件人批量插入 `tc_notify_recipient`。
- `updateStatus` / `deleteByBiz` 供任务模块在任务取消、终止等场景清理或同步状态，内部通过 `selectIdsByBiz` 反查 Job 并批量更新、逻辑删除。

### 4.2 NotifyRenderer 与模板实现

```java
public interface NotifyRenderer {
    RenderedMsg render(byte bizType, byte channel, JSONObject payload);
}
```

当前提供 `TemplateNotifyRenderer` 实现：按 `biz_type` + `channel` 读取 `tc_notify_template`，用 payload 中的键值做 `${key}` 占位符替换。

### 4.3 RenderedMsg 结构

```java
public class RenderedMsg {
    private final String title;
    private final String content;
    private final String externalTemplateId;
}
```

`externalTemplateId` 会透传到渠道驱动，用于钉钉等三方模板消息。

### 4.4 渠道驱动（NotifyDriver SPI）

```java
public interface NotifyDriver {
    byte channel();

    SendResult send(String userId, RenderedMsg message, JSONObject payload);

    Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload);
}
```

驱动按照 `channel()` 注册，由分发器在运行期注入。

### 4.5 SendResult

```java
public class SendResult {
    private final boolean ok;
    private final String error;
    private final String externalMsgId;
    private final boolean retryable;
    public static SendResult success();
    public static SendResult success(String externalMsgId);
    public static SendResult failure(String error);
    public static SendResult failure(String error, String externalMsgId);
    public static SendResult permanentFailure(String error);
}
```

`externalMsgId` 会写回 `tc_notify_recipient.external_msgid`，便于后续对账或补偿。

### 4.6 NotifyDispatcher（分发器）

- `lockDueJobs(limit)` 使用 `FOR UPDATE SKIP LOCKED` 拉取待执行的 Job，默认每 3 秒调度一次。
- 对每个 Job：解析 payload → `NotifyRenderer` 渲染 → 查询收件人明细 → 按渠道获取 `NotifyDriver` 并调用 `sendBatch`。
- 收到的 `SendResult` 会逐条写入收件人状态及 `external_msgid`。失败结果若标记为 `retryable` 才会触发重试；全部成功或仅存在不可重试的失败则尝试 `scheduleNextTimeoutReminder`，若未触发重复提醒则调用 `markSuccess`。
- 失败时根据 `retryCount` 计算下一次执行时间，回退序列为 1,3,5,10,30,60,180（分钟）。
- `scheduleNextTimeoutReminder` 会在节点仍为办理中的情况下重置 `next_run_time` 并清零 `retry_count`，从而实现持续提醒。

### 4.7 DingTalkRobotDriver

- 启动时加载并缓存最新一条启用的 `tc_notify_channel_config`，要求 `config_json` 至少包含 `baseUrl`、`agentId`、`corpId`。
- 仅支持使用 `sys_user.username` 作为钉钉 userid，若未配置则返回失败；不再拼装电话/邮箱。
- 自动将标题截断至 20 个字符并记录 warn 日志。
- `sendBatch` 会按业务用户 ID 去重并合并为一次三方调用，成功后把三方返回的 `msgid` 写入 `externalMsgId`。
- 请求体中的 `template_params` 直接由 payload 填充，并透传 `RenderedMsg.externalTemplateId` 作为三方模板 ID。

### 4.8 OAHttpDriver

当前实现仍为占位符，直接返回成功，后续接入 OA 平台时可在此封装 HTTP 调用。

### 4.9 NotifyProperties（平台链接）

`NotifyProperties.platformUrl` 可注入到 payload 中，模板可通过 `${platformUrl}` 渲染跳转链接。
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
○ Content：【星地协同平台】您好，您参与的任务\${taskName}节点\${nodeName}已经超时\${overtime}分钟（payload 同时保留原始 \${maxDuration} 可按需展示），请尽快登录系统进行处理，感谢支持！

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
  "platformUrl": "https://dspp.example.com/tasks/1001",
  "finishTime": "2025-08-02 20:30",
  "reason": "B节点决策为不执行",
  "nodeInstId": 8882,
  "nodeName": "资源确认",
  "maxDuration": 60,
  "timeoutRemind": 30
}
分发器在发送节点超时通知前会根据节点办理时长追加 `overtime` 字段，并将 `maxDuration` 覆写为实时超时时长。

### 5.3 钉钉 external_tpl_id 配置

`tc_notify_template.external_tpl_id` 需与第三方模板保持一致，当前约定如下：

| BizTypeEnum      | external_tpl_id | 说明           |
|-----------------|-----------------|----------------|
| TASK_CREATED     | SE1001          | 任务创建通知   |
| NODE_DONE        | SE1002          | 节点完成通知   |
| ORBIT_REMIND     | SE1003          | 卫星进站提醒   |
| TASK_FINISHED    | SE1004          | 任务完成通知   |
| TASK_ABNORMAL    | SE1005          | 任务异常结束   |
| NODE_TIMEOUT     | SE1006          | 节点超时提醒   |

后续如有新增业务类型，请同步维护该映射表。

## 6. 去重、重试与限流

● 去重（dedup\_key）：同一业务事件在同一渠道仅生成一次 Job。若未传 `dedup_key` 则总是新建；传入重复键会直接返回已有 jobId。
● 重试：失败后按 1,3,5,10,30,60,180（分钟）回退；节点仍在办理且命中 `timeoutRemind` 时会重置 `retry_count` 并重新排期。
● 限流：渠道驱动内部实现（例如钉钉机器人每分钟 N 条）；分发器按渠道分桶发送（可选）。
● 取消：当任务被取消或异常结束，如需停止未来计划的提醒，将对应 Job status=3（取消）或 status=4（异常结束），收件人明细状态与之对应。

## 7. 与任务管理的对接点（关键调用）

● `TcTaskManagerServiceImpl.createTask`：写入 TASK_CREATED Job，并调用 `scheduleOrbitReminders` 计划进站提醒。
● `TcTaskManagerServiceImpl.submitTaskAction`：节点完成时推送 NODE_DONE，任务完成/异常时分别推送 TASK_FINISHED、TASK_ABNORMAL。
● `TcTaskManagerServiceImpl.scheduleNodeTimeout`：节点激活时根据 `maxDuration` 入库 NODE_TIMEOUT Job，并携带 `timeoutRemind`。
● `TcTaskManagerServiceImpl.updateNotifyJobsForTask`：任务终止/取消时批量调用 `notifyService.updateStatus` 标记待发 Job 为已终止。
● `TcTaskManagerServiceImpl.cancelOrbitReminders`：根据任务 ID 调用 `notifyService.deleteByBiz` 清理尚未发送的进站提醒。
● `NotifyDispatcher`：常驻调度器，按固定频率扫描 Job 并委派渠道驱动发送。

## 8. 代码骨架（示例）

```java
@Service
public class NotifyServiceImpl implements NotifyService {
    @Autowired
    private NotifyJobMapper notifyJobMapper;
    @Autowired
    private NotifyRecipientMapper notifyRecipientMapper;

    @Override
    @Transactional
    public long enqueue(byte bizType, long bizId, byte channel,
                        JSONObject payload, List<String> userIds,
                        String dedupKey, LocalDateTime nextRunTime,
                        String operator) {
        Long jobId = null;
        if (dedupKey != null && !dedupKey.isEmpty()) {
            jobId = notifyJobMapper.selectIdByDedupKey(dedupKey);
        }
        if (jobId != null) {
            return jobId;
        }
        NotifyJob job = new NotifyJob();
        job.setBizType(bizType);
        job.setBizId(bizId);
        job.setChannel(channel);
        job.setPayload(payload.toJSONString());
        job.setDedupKey(dedupKey);
        job.setNextRunTime(nextRunTime);
        job.setStatus(NotifyJobStatusEnum.WAITING.getCode());
        job.setRetryCount(0);
        job.initBase(true, operator);
        notifyJobMapper.insert(job);
        jobId = job.getId();
        List<String> distinctUsers = userIds.stream().distinct().collect(Collectors.toList());
        notifyRecipientMapper.batchInsert(jobId, distinctUsers, operator);
        return jobId;
    }
}

@Component
public class NotifyDispatcher {
    @Scheduled(fixedDelay = 3000)
    public void dispatch() {
        List<NotifyJob> jobs = jobMapper.lockDueJobs(200);
        for (NotifyJob job : jobs) {
            JSONObject payload = JSONObject.parseObject(job.getPayload());
            enrichTimeoutPayload(job, payload);
            RenderedMsg msg = renderer.render(job.getBizType(), job.getChannel(), payload);
            List<NotifyRecipient> recipients = recMapper.findByJobId(job.getId());
            NotifyDriver driver = driverMap.get(job.getChannel());
            Map<Long, SendResult> results = driver.sendBatch(recipients, msg, payload);
            boolean allOk = true;
            for (NotifyRecipient recipient : recipients) {
                SendResult sr = results.get(recipient.getId());
                boolean ok = sr != null && sr.isOk();
                recMapper.updateStatus(recipient.getId(), ok, sr == null ? "send result missing" : sr.getError(),
                        sr == null ? null : sr.getExternalMsgId());
                if (!ok) {
                    allOk = false;
                }
            }
            if (allOk) {
                if (!scheduleNextTimeoutReminder(job, payload)) {
                    jobMapper.markSuccess(job.getId());
                }
            } else {
                int nextRetry = job.getRetryCount() + 1;
                jobMapper.scheduleRetry(job.getId(), calcNext(nextRetry), nextRetry);
            }
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
