package com.zjlab.dataservice.modules.toolbox.enumerate;

public enum ToolBoxStatus {
    INIT(1, "初始化"),
    EDITING(2, "编辑中"),
    READY(3, "准备就绪"),
    RUNNING(4, "运行中"),
    SUCCESS(5, "成功"),
    FAIL(6, "失败"),
    DELETE(7, "已删除"),
    WAITING(8, "等待中"),
    STOP(9, "已停止");

    private int code;
    private String description;

    // 构造函数
    ToolBoxStatus(int code, String description) {
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

    public static ToolBoxStatus fromString(String status) {
        for (ToolBoxStatus s : values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null; // 或者抛出一个异常，或者返回一个特殊的枚举值
    }

    // toString 方法，返回枚举的描述
    @Override
    public String toString() {
        return description;
    }
}
