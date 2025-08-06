package com.zjlab.dataservice.modules.myspace.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageApplyStatusEnum {

    UNAPPLY(0, "未申请"),
    APPLYING(1, "申请中"),
    APPLIED(2, "已申请"),
    EXPIRED(3,"已失效"),
    ;

    private final Integer status;

    private final String name;

    public static ImageApplyStatusEnum getEnumByStatus(Integer status){
        for (ImageApplyStatusEnum applyStatusEnum : ImageApplyStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum;
            }
        }
        return null;
    }

    public static String getName(Integer status){
        for (ImageApplyStatusEnum applyStatusEnum : ImageApplyStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum.getName();
            }
        }
        return null;
    }
}
