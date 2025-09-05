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
        // todo 这里简化为直接成功返回,后续需要接入具体send逻辑
        return SendResult.success();
    }
}

