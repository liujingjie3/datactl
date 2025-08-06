package com.zjlab.dataservice.common.system.query;

/**
 * data表中的type，对应到type表中的parent_id字段，用于区分不同数据类型枚举
 */
public enum DataTypeEnum {
    DATATYPE_1(1, "1000000"),
    DATATYPE_2(2, "2000000"),
    DATATYPE_3(3, "3000000"),
    DATATYPE_4(4, "4000000");

    private int type;
    private String value;

    DataTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    // 获取与type对应的DataTypeEnum枚举实例
    public static DataTypeEnum fromType(int type) {
        for (DataTypeEnum dataType : values()) {
            if (dataType.getType() == type) {
                return dataType;
            }
        }
        throw new IllegalArgumentException("No matching data type for the given type: " + type);
    }
}