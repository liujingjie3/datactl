package com.zjlab.dataservice.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum ApplyStatusEnum {

    /**
     * 未处理
     */
    UNTREATED(0, "未处理"),

    /**
     * 已同意
     */
    AGREED(1, "已同意"),
    /**
     * 拒绝
     */
    REFUSE(2, "拒绝");



    /**
     * 类型 0未处理，1 已同意，2拒绝
     */
    int type;

    /**
     * 解释
     */
    String name;




    /**
     * 构造器
     *
     * @param type 类型
     * @param code 编码(请求方式)
     */
//    ApplyStatusEnum(int type, String code) {
//        this.type = type;
//        this.code = code;
//    }


    /**
     * 根据请求名称匹配
     *
     * @param methodName 请求名称
     * @return Integer 类型
     */
//    public static Integer getTypeByMethodName(String methodName) {
//        for (OperateTypeEnum e : OperateTypeEnum.values()) {
//            if (methodName.startsWith(e.getCode())) {
//                return e.getType();
//            }
//        }
//        return CommonConstant.OPERATE_TYPE_1;
//    }
}
