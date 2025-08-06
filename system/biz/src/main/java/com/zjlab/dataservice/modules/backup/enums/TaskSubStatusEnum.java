package com.zjlab.dataservice.modules.backup.enums;

import lombok.Getter;

@Getter
public enum TaskSubStatusEnum {

    SUCCESS("SUCCESS", "执行成功结束"),
    FAIL("FAIL", "执行失败结束"),
    STOP("STOP", "人工终止"),

    ;

    private final String code;
    private final String message;

    private TaskSubStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
