package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

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
    public SendResult send(String userId, String title, String content, JSONObject payload) {
        // 简化处理为成功
        return SendResult.success();
    }
}

