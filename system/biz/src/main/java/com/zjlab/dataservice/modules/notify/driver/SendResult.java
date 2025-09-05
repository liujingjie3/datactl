package com.zjlab.dataservice.modules.notify.driver;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 发送结果
 */
@Data
@AllArgsConstructor
public class SendResult {
    private boolean ok;
    private String error;

    public static SendResult success() {
        return new SendResult(true, null);
    }

    public static SendResult failure(String error) {
        return new SendResult(false, error);
    }
}

