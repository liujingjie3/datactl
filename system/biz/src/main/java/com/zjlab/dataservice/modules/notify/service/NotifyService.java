package com.zjlab.dataservice.modules.notify.service;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.util.List;

public interface NotifyService {
    long enqueue(byte bizType, long bizId, byte channel,
                 JSONObject payload, List<String> userIds,
                 String dedupKey, LocalDateTime nextRunTime,
                 String operator);
}

