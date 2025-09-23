package com.zjlab.dataservice.modules.notify.driver;

import lombok.Data;

/**
 * 发送结果
 */
@Data
public class SendResult {
    private final boolean ok;
    private final String error;
    private final String externalMsgId;

    private SendResult(boolean ok, String error, String externalMsgId) {
        this.ok = ok;
        this.error = error;
        this.externalMsgId = externalMsgId;
    }

    public static SendResult success() {
        return new SendResult(true, null, null);
    }

    public static SendResult success(String externalMsgId) {
        return new SendResult(true, null, externalMsgId);
    }

    public static SendResult failure(String error) {
        return new SendResult(false, error, null);
    }

    public static SendResult failure(String error, String externalMsgId) {
        return new SendResult(false, error, externalMsgId);
    }
}

