package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;

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
}

