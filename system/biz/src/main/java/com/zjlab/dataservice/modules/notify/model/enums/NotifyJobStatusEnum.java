package com.zjlab.dataservice.modules.notify.model.enums;

/**
 * 通知任务状态
 */
public enum NotifyJobStatusEnum {
    WAITING((byte) 0),
    SUCCESS((byte) 1),
    FAILED((byte) 2),
    CANCELED((byte) 3),
    ABORTED((byte) 4);

    private final byte code;

    NotifyJobStatusEnum(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}

