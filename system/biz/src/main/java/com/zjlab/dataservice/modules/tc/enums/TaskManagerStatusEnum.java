package com.zjlab.dataservice.modules.tc.enums;

/**
 * 任务状态
 * 0-运行中 / 1-结束 / 2-异常结束 / 3-取消
 */
public enum TaskManagerStatusEnum {
    RUNNING(0, "运行中"),
    FINISHED(1, "结束"),
    ABNORMAL_END(2, "异常结束"),
    CANCELED(3, "取消");

    private final int code;
    private final String desc;

    TaskManagerStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskManagerStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskManagerStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
