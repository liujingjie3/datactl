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
    private final boolean retryable;

    private SendResult(boolean ok, String error, String externalMsgId, boolean retryable) {
        this.ok = ok;
        this.error = error;
        this.externalMsgId = externalMsgId;
        this.retryable = retryable;
    }

    public static SendResult success() {
        return new SendResult(true, null, null, false);
    }

    public static SendResult success(String externalMsgId) {
        return new SendResult(true, null, externalMsgId, false);
    }

    public static SendResult failure(String error) {
        return new SendResult(false, error, null, true);
    }

    public static SendResult failure(String error, String externalMsgId) {
        return new SendResult(false, error, externalMsgId, true);
    }

    public static SendResult permanentFailure(String error) {
        return new SendResult(false, error, null, false);
    }

    public static SendResult permanentFailure(String error, String externalMsgId) {
        return new SendResult(false, error, externalMsgId, false);
    }
}

