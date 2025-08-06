package com.zjlab.dataservice.modules.taskplan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CXStatusEnum {

    DOING(1, "进行中"),
    FAIL(2, "失败"),
    SUCCESS(3, "成功"),
    CONFIRE(4, "已确认")
    ;

    private final Integer status;

    private final String name;

    public static CXStatusEnum getEnumByStatus(Integer status){
        for (CXStatusEnum applyStatusEnum : CXStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum;
            }
        }
        return null;
    }

    public static String getName(Integer status){
        for (CXStatusEnum applyStatusEnum : CXStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum.getName();
            }
        }
        return null;
    }
}
