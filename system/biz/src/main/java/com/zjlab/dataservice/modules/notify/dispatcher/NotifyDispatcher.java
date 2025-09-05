package com.zjlab.dataservice.modules.notify.dispatcher;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.driver.NotifyDriver;
import com.zjlab.dataservice.modules.notify.driver.SendResult;
import com.zjlab.dataservice.modules.notify.mapper.NotifyJobMapper;
import com.zjlab.dataservice.modules.notify.mapper.NotifyRecipientMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyJob;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.NotifyRenderer;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;
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

    private Map<Byte, NotifyDriver> driverMap;

    @Scheduled(fixedDelay = 3000)
    public void dispatch() {
        ensureDriverMap();
        List<NotifyJob> jobs = jobMapper.lockDueJobs(200);
        for (NotifyJob j : jobs) {
            RenderedMsg msg = renderer.render(j.getBizType(), j.getChannel(),
                    JSONObject.parseObject(j.getPayload()));
            List<NotifyRecipient> rs = recMapper.findByJobId(j.getId());
            boolean allOk = true;
            for (NotifyRecipient r : rs) {
                NotifyDriver driver = driverMap.get(j.getChannel());
                SendResult sr = driver.send(r.getUserId(), msg.getTitle(), msg.getContent(),
                        JSONObject.parseObject(j.getPayload()));
                recMapper.updateStatus(r.getId(), sr.isOk(), sr.getError());
                if (!sr.isOk()) {
                    allOk = false;
                }
            }
            if (allOk) {
                jobMapper.markSuccess(j.getId());
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
        int[] minutes = new int[]{1, 5, 30, 60, 180};
        int idx = Math.min(retryCount - 1, minutes.length - 1);
        return LocalDateTime.now().plusMinutes(minutes[idx]);
    }
}

