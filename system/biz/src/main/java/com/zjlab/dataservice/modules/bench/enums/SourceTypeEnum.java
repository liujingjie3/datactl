package com.zjlab.dataservice.modules.bench.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SourceTypeEnum {
    OPEN("open", "开源"),
    CLOSED("closed", "闭源"),
    ;

    private final String type;

    private final String name;

    public static SourceTypeEnum getEnumByType(String type){
        for (SourceTypeEnum taskTypeEnum : SourceTypeEnum.values()){
            if (taskTypeEnum.getType().equals(type)){
                return taskTypeEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (SourceTypeEnum taskTypeEnum : SourceTypeEnum.values()){
            if (taskTypeEnum.getType().equals(type)){
                return taskTypeEnum.getName();
            }
        }
        return null;
    }
}
