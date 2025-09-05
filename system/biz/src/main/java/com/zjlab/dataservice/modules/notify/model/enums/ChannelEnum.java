package com.zjlab.dataservice.modules.notify.model.enums;

/**
 * 渠道枚举
 */
public enum ChannelEnum {
    OA(0, "OA"),
    DINGTALK(1, "DINGTALK");

    private final int code;
    private final String desc;

    ChannelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ChannelEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ChannelEnum c : values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }
}

