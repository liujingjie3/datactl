package com.zjlab.dataservice.common.system.query;

/**
 * data表中的type，对应到type表中的parent_id字段，用于区分不同数据类型枚举
 */
public enum GeoTypeEnum {
    DATATYPE_1("1050000", "1"),
    DATATYPE_2("1010300", "2"),
    DATATYPE_3("1020000", "3"),
    DATATYPE_4("1060203", "4"),
    DATATYPE_5("1060100", "5"),
    DATATYPE_6("1020200", "6"),
    DATATYPE_7("1050601", "7"),
    DATATYPE_8("1020100", "8"),
    DATATYPE_9("1020300", "9"),
    DATATYPE_10("1050300", "10"),
    DATATYPE_11("1080101", "11"),
    DATATYPE_12("1030000", "12"),
    DATATYPE_13("1080200", "13"),
    DATATYPE_14("1080300", "14"),
    DATATYPE_15("1080000", "15"),
    DATATYPE_16("1090000", "16"),
    DATATYPE_17("1070200", "17"),
    DATATYPE_18("1050105", "18"),
    DATATYPE_19("1050101", "19"),
    DATATYPE_20("1050700", "20"),
    DATATYPE_21("1040200", "21"),
    DATATYPE_22("1050100", "22"),
    DATATYPE_23("1050500", "23"),
    DATATYPE_24("1050103", "24");

    private final String code;
    private final String description;

    GeoTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(String code) {
        for (GeoTypeEnum dt : GeoTypeEnum.values()) {
            if (dt.getCode().equals(code)) {
                return dt.getDescription();
            }
        }
        throw new IllegalArgumentException("No matching data type for the given type: " + code);
    }

}
