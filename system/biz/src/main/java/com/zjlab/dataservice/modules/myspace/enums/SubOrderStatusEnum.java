package com.zjlab.dataservice.modules.myspace.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubOrderStatusEnum {

    DOING(1, "审核中"),
    DONE(2, "审核通过"),
    UNDONE(3, "审核未通过"),
    LOSS(4, "已失效")
    ;

    private final Integer status;

    private final String name;

    public static SubOrderStatusEnum getEnumByStatus(Integer status){
        for (SubOrderStatusEnum applyStatusEnum : SubOrderStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum;
            }
        }
        return null;
    }

    public static String getName(Integer status){
        for (SubOrderStatusEnum applyStatusEnum : SubOrderStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum.getName();
            }
        }
        return null;
    }
}
