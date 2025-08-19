package com.zjlab.dataservice.modules.task.enums;

/**
 * 任务状态
 * 0-运行中 / 1-结束 / 2-异常结束 / 3-取消
 */
public enum TaskStatusEnum {
    RUNNING(0, "运行中"),
    FINISHED(1, "结束"),
    ABNORMAL_END(2, "异常结束"),
    CANCELED(3, "取消");

    private final int code;
    private final String desc;

    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
