package com.zjlab.dataservice.modules.backup.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {

    INIT("INIT", "初始化中"),
    WAIT("WAIT", "等待执行"),
    EXECUTING("EXECUTING", "运行中"),
    FINISH("FINISH", "已结束"),
    STOPPING("STOPING", "停止中"),
    DELETED("DELETED", "已删除"),
    CREATE_FAIL("CREATE_FAIL", "创建失败"),
    START_FAIL("START_FAIL", "启动失败"),
    RESTART_FAIL("RESTART_FAIL", "重启失败"),
    STOP_FAIL("STOP_FAIL", "停止失败")

    ;

    private final String code;
    private final String message;

    private TaskStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
