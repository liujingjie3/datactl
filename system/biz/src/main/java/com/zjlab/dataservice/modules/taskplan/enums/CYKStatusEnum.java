package com.zjlab.dataservice.modules.taskplan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CYKStatusEnum {

    SUCCESS(1, "成功"),
    PATIAL_SUCCESS(2, "部分成功"),
    FAIL(3, "失败")
    ;

    private final Integer status;

    private final String name;

    public static CYKStatusEnum getEnumByStatus(Integer status){
        for (CYKStatusEnum applyStatusEnum : CYKStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum;
            }
        }
        return null;
    }

    public static String getName(Integer status){
        for (CYKStatusEnum applyStatusEnum : CYKStatusEnum.values()){
            if (applyStatusEnum.getStatus().equals(status)){
                return applyStatusEnum.getName();
            }
        }
        return null;
    }
}
