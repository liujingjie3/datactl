package com.zjlab.dataservice.modules.backup.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SourceTypeEnum {
    MYSQL("mysql", "MySQL数据库"),
    POSTGRESQL("postgresql", "PostgreSQL数据库"),
    MINIO("minio", "Minio对象存储"),
    ;

    private final String type;

    private final String name;

    public static SourceTypeEnum getEnumByType(String type){
        for (SourceTypeEnum sourceTypeEnum : SourceTypeEnum.values()){
            if (sourceTypeEnum.getType().equals(type)){
                return sourceTypeEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (SourceTypeEnum sourceTypeEnum : SourceTypeEnum.values()){
            if (sourceTypeEnum.getType().equals(type)){
                return sourceTypeEnum.getName();
            }
        }
        return null;
    }
}
