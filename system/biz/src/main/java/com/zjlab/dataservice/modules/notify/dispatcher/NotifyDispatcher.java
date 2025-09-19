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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
            RenderedMsg msg = renderer.render(j.getBizType(), j.getChannel(), payload);
            List<NotifyRecipient> rs = recMapper.findByJobId(j.getId());
            boolean allOk = true;
            for (NotifyRecipient r : rs) {
                NotifyDriver driver = driverMap.get(j.getChannel());
                SendResult sr = driver.send(r.getUserId(), msg.getTitle(), msg.getContent(), payload);
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
        if (status == null) {
            return false;
        }
        // 节点状态：0-未激活、1-进行中、2-已完成、3-已取消、4-异常结束
        // 仅当节点仍处于未激活或进行中时，才需要继续进行超时提醒。
        if (status == 0 || status == 1) {
            LocalDateTime nextTime = LocalDateTime.now().plusMinutes(interval);
            jobMapper.reschedule(job.getId(), nextTime);
            return true;
        }
        return false;
    }
}

