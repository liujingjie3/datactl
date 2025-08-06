package com.zjlab.dataservice.modules.myspace.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageTypeEnum {

    GF2("GF2", "光学影像"),
    GF3("GF3", "SAR影像"),
    GF2_GF3_UNION("GF2-GF3-union", "光学/SAR联合数据")    //统计大屏覆盖率用
            ;

    private final String type;

    private final String name;

    public static ImageTypeEnum getEnumByType(String type){
        for (ImageTypeEnum imageTypeEnum : ImageTypeEnum.values()){
            if (imageTypeEnum.getType().equals(type)){
                return imageTypeEnum;
            }
        }
        return null;
    }

    public static String getName(String type){
        for (ImageTypeEnum imageTypeEnum : ImageTypeEnum.values()){
            if (imageTypeEnum.getType().equals(type)){
                return imageTypeEnum.getName();
            }
        }
        return null;
    }
}
