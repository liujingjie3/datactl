package com.zjlab.dataservice.modules.myspace.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    DOING(1, "申请中"),
    DONE(2, "已完成"),
    ;

    private final Integer status;

    private final String name;

    public static OrderStatusEnum getEnumByStatus(Integer status){
        for (OrderStatusEnum applyStatusEnum : OrderStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum;
            }
        }
        return null;
    }

    public static String getName(Integer status){
        for (OrderStatusEnum applyStatusEnum : OrderStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum.getName();
            }
        }
        return null;
    }
}
