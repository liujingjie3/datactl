package com.zjlab.dataservice.modules.notify.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.mapper.NotifyJobMapper;
import com.zjlab.dataservice.modules.notify.mapper.NotifyRecipientMapper;
import com.zjlab.dataservice.modules.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 */
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
        Long jobId = notifyJobMapper.selectIdByDedupKey(dedupKey);
        if (jobId != null) {
            return jobId;
        }
        jobId = notifyJobMapper.insertJob(bizType, bizId, channel, payload.toJSONString(),
                dedupKey, nextRunTime, operator);
        List<String> distinctUsers = userIds.stream().distinct().collect(Collectors.toList());
        notifyRecipientMapper.batchInsert(jobId, distinctUsers, operator);
        return jobId;
    }
}

