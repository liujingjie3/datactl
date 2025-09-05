package com.zjlab.dataservice.modules.notify.model.enums;

/**
 * 业务事件类型
 */
public enum BizTypeEnum {
    TASK_CREATED(0, "TASK_CREATED"),
    NODE_DONE(1, "NODE_DONE"),
    ORBIT_REMIND(2, "ORBIT_REMIND"),
    TASK_FINISHED(3, "TASK_FINISHED"),
    TASK_ABNORMAL(4, "TASK_ABNORMAL"),
    NODE_TIMEOUT(5, "NODE_TIMEOUT");

    private final int code;
    private final String desc;

    BizTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BizTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (BizTypeEnum t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        return null;
    }
}

