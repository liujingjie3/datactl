package com.zjlab.dataservice.modules.notify.dispatcher;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.driver.NotifyDriver;
import com.zjlab.dataservice.modules.notify.driver.SendResult;
import com.zjlab.dataservice.modules.notify.mapper.NotifyJobMapper;
import com.zjlab.dataservice.modules.notify.mapper.NotifyRecipientMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyJob;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.model.enums.BizTypeEnum;
import com.zjlab.dataservice.modules.notify.render.NotifyRenderer;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskNodeInstMapper;
import com.zjlab.dataservice.modules.tc.model.dto.NodeNotifyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知分发器
 */
@Component
public class NotifyDispatcher {

    @Autowired
    private NotifyJobMapper jobMapper;
    @Autowired
    private NotifyRecipientMapper recMapper;
    @Autowired
    private NotifyRenderer renderer;
    @Autowired
    private List<NotifyDriver> drivers;
    @Autowired
    private TcTaskNodeInstMapper taskNodeInstMapper;

    private Map<Byte, NotifyDriver> driverMap;

    @Scheduled(fixedDelay = 3000)
    public void dispatch() {
        ensureDriverMap();
        List<NotifyJob> jobs = jobMapper.lockDueJobs(200);
        for (NotifyJob j : jobs) {
            JSONObject payload = JSONObject.parseObject(j.getPayload());
            enrichTimeoutPayload(j, payload);
            RenderedMsg msg = renderer.render(j.getBizType(), j.getChannel(), payload);
            List<NotifyRecipient> rs = recMapper.findByJobId(j.getId());
            boolean allOk = true;
            for (NotifyRecipient r : rs) {
                NotifyDriver driver = driverMap.get(j.getChannel());
                SendResult sr = driver.send(r.getUserId(), msg, payload);
                recMapper.updateStatus(r.getId(), sr.isOk(), sr.getError());
                if (!sr.isOk()) {
                    allOk = false;
                }
            }
            if (allOk) {
                if (!scheduleNextTimeoutReminder(j, payload)) {
                    jobMapper.markSuccess(j.getId());
                }
            } else {
                int nextRetry = j.getRetryCount() + 1;
                jobMapper.scheduleRetry(j.getId(), calcNext(nextRetry), nextRetry);
            }
        }
    }

    private void ensureDriverMap() {
        if (driverMap == null) {
            driverMap = new HashMap<>();
            for (NotifyDriver d : drivers) {
                driverMap.put(d.channel(), d);
            }
        }
    }

    private LocalDateTime calcNext(int retryCount) {
        int[] minutes = new int[]{1, 3, 5, 10, 30, 60, 180};
        int idx = Math.min(retryCount - 1, minutes.length - 1);
        return LocalDateTime.now().plusMinutes(minutes[idx]);
    }

    private boolean scheduleNextTimeoutReminder(NotifyJob job, JSONObject payload) {
        if (job.getBizType() == null
                || job.getBizType() != (byte) BizTypeEnum.NODE_TIMEOUT.getCode()) {
            return false;
        }
        if (payload == null) {
            return false;
        }
        Integer interval = payload.getInteger("timeoutRemind");
        if (interval == null || interval <= 0) {
            return false;
        }
        Integer status = taskNodeInstMapper.selectNodeInstStatus(job.getBizId());


        if (status == null || status != 1) {
                return false;
        }
        LocalDateTime nextTime = LocalDateTime.now().plusMinutes(interval);
        jobMapper.reschedule(job.getId(), nextTime);
        return true;
    }

    private void enrichTimeoutPayload(NotifyJob job, JSONObject payload) {
        if (job.getBizType() == null
                || job.getBizType() != (byte) BizTypeEnum.NODE_TIMEOUT.getCode()
                || payload == null) {
            return;
        }
        Integer originalDuration = payload.getInteger("maxDuration");
        LocalDateTime deadline = resolveDeadline(job.getBizId(), originalDuration);
        if (deadline == null) {
            return;
        }
        long overtime = Duration.between(deadline, LocalDateTime.now()).toMinutes();
        if (overtime < 0) {
            overtime = 0;
        }
        payload.put("overtime", overtime);
        payload.put("maxDuration", overtime);
    }

    private LocalDateTime resolveDeadline(Long nodeInstId, Integer originalDuration) {
        NodeNotifyContext context = taskNodeInstMapper.selectNodeNotifyContext(nodeInstId);
        if (context == null) {
            return null;
        }
        LocalDateTime startedAt = context.getStartedAt();
        if (startedAt == null || originalDuration == null) {
            return null;
        }
        return startedAt.plusMinutes(originalDuration);
    }
}

