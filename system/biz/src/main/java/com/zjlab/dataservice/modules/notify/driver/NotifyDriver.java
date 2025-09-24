package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;

import java.util.List;
import java.util.Map;

/**
 * 通知发送驱动
 */
public interface NotifyDriver {

    /** 渠道 */
    byte channel();

    Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload);
}

