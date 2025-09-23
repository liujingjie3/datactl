package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知发送驱动
 */
public interface NotifyDriver {

    /** 渠道 */
    byte channel();

    /**
     * 发送消息
     */
    SendResult send(String userId, RenderedMsg message, JSONObject payload);

    default Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload) {
        if (recipients == null || recipients.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, SendResult> result = new HashMap<>(recipients.size());
        for (NotifyRecipient recipient : recipients) {
            if (recipient == null) {
                continue;
            }
            result.put(recipient.getId(), send(recipient.getUserId(), message, payload));
        }
        return result;
    }
}

