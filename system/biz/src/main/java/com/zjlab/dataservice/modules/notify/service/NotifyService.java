package com.zjlab.dataservice.modules.notify.service;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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

