package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
    public Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload) {
        // TODO 暂未接入 OA 通知发送，后续补充具体实现
        return Collections.emptyMap();
    }
}

