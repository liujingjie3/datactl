package com.zjlab.dataservice.modules.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskTypeEnum {
    CLASSIFY("classify", "分类"),
    SEGMENT("segment", "分割"),
    TEXT("text", "图文"),
    FILTER("filter", "图筛"),
    ;

    private final String type;

    private final String name;

    public static TaskTypeEnum getEnumByType(String type){
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()){
            if (taskTypeEnum.getType().equals(type)){
                return taskTypeEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()){
            if (taskTypeEnum.getType().equals(type)){
                return taskTypeEnum.getName();
            }
        }
        return null;
    }
}
