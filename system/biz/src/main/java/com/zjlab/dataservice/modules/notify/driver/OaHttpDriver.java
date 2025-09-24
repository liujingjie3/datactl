package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OA通知驱动
 */
@Component
public class OaHttpDriver implements NotifyDriver {

    @Override
    public byte channel() {
        return 0; // OA
    }

    @Override
    public SendResult send(String userId, RenderedMsg message, JSONObject payload) {
        // todo 这里简化为直接成功返回,后续需要接入具体send逻辑
        return SendResult.success();
    }

    @Override
    public Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload) {
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

