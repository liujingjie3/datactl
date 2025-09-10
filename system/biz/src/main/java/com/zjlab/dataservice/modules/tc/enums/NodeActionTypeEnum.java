package com.zjlab.dataservice.modules.tc.enums;

/**
 * 节点操作类型
 * 0-上传 / 1-选择圈次计划 / 2-决策 / 3-文本填写 / 4-修改遥控指令单
 */
public enum NodeActionTypeEnum {
    UPLOAD(0, "上传"),
    SELECT_ORBIT_PLAN(1, "选择圈次计划"),
    DECISION(2, "决策"),
    TEXT(3, "文本填写"),
    MODIFY_REMOTE_CMD(4, "修改遥控指令单");

    private final int code;
    private final String desc;

    NodeActionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NodeActionTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeActionTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
