package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 钉钉机器人驱动
 */
@Component
public class DingTalkRobotDriver implements NotifyDriver {

    @Override
    public byte channel() {
        return 1; // 钉钉
    }

    @Override
    public SendResult send(String userId, String title, String content, JSONObject payload) {
        // todo 这里简化为直接成功返回,后续需要接入具体send逻辑
        return SendResult.success();
    }
}

