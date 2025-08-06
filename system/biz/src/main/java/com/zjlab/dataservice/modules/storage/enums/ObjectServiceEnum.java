package com.zjlab.dataservice.modules.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ObjectServiceEnum {


    MINIO("minio", "minio对象"),
            ;

    private final String type;

    private final String name;

    public static ObjectServiceEnum getEnumByType(String type){
        for (ObjectServiceEnum objectServiceEnum : ObjectServiceEnum.values()){
            if (objectServiceEnum.getType().equals(type)){
                return objectServiceEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (ObjectServiceEnum objectServiceEnum : ObjectServiceEnum.values()){
            if (objectServiceEnum.getType().equals(type)){
                return objectServiceEnum.getName();
            }
        }
        return null;
    }
}
