package com.zjlab.dataservice.modules.tc.enums;

/**
 * Tabs for task manager list
 */
public enum TaskManagerTabEnum {
    ALL("all"),
    STARTED_BY_ME("startedByMe"),
    TODO("todo"),
    HANDLED("handled");

    private final String value;

    TaskManagerTabEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TaskManagerTabEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TaskManagerTabEnum tab : values()) {
            if (tab.value.equals(value)) {
                return tab;
            }
        }
        return null;
    }
}
