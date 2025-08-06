package com.zjlab.dataservice.modules.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileServiceEnum {

    SERVER("server", "服务器文件"),
    HDFS("hdfs", "hdfs文件"),
    ;

    private final String type;

    private final String name;

    public static FileServiceEnum getEnumByType(String type){
        for (FileServiceEnum fileServiceEnum : FileServiceEnum.values()){
            if (fileServiceEnum.getType().equals(type)){
                return fileServiceEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (FileServiceEnum fileServiceEnum : FileServiceEnum.values()){
            if (fileServiceEnum.getType().equals(type)){
                return fileServiceEnum.getName();
            }
        }
        return null;
    }
}
