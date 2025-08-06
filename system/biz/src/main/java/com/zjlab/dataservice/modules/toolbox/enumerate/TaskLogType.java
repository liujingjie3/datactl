package com.zjlab.dataservice.modules.toolbox.enumerate;

public enum TaskLogType {
    FAIL(1, "失败"),
    SUCCESS(2, "成功");


    private int code;
    private String description;

    // 构造函数
    TaskLogType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 可以根据code查找枚举值
    public static ToolBoxStatus fromCode(int code) {
        for (ToolBoxStatus toolBoxStatus : ToolBoxStatus.values()) {
            if (toolBoxStatus.getCode() == code) {
                return toolBoxStatus;
            }
        }
        throw new IllegalArgumentException("No toolBoxStatus found for code " + code);
    }

    // 可以根据description查找枚举值
    public static ToolBoxStatus fromDescription(String description) {
        for (ToolBoxStatus toolBoxStatus : ToolBoxStatus.values()) {
            if (toolBoxStatus.getDescription().equalsIgnoreCase(description)) {
                return toolBoxStatus;
            }
        }
        throw new IllegalArgumentException("No toolBoxStatus found for description '" + description + "'");
    }

    // toString 方法，返回枚举的描述
    @Override
    public String toString() {
        return description;
    }
}
