package com.zjlab.dataservice.common.util.storage;


/**
 * @author houcongcong@zhejianglab.com
 * @date 2023年04月10日 4:07 下午
 * @Description
 */
public class StorageUnitConveterUtil {

    public static StorageUnit convertKBToAnyUnit(double source) {

        long bytes = (long) (source *UnitEnum.KB.getBytes());
        UnitEnum targetUnit = UnitEnum.B;
        double resultBytes = (double) bytes / targetUnit.getBytes();
        while (resultBytes >= 1024) {
            targetUnit = targetUnit.getNextUnit();
            resultBytes /= 1024;
        }
        double roundedResult = Math.round(resultBytes * 100) / 100.0; // 四舍五入保留两位小数
        return new StorageUnit(roundedResult, targetUnit.name());
    }

    public static String convertKBToAnyUnitStr(double source) {

        long bytes = (long) (source *UnitEnum.KB.getBytes());
        UnitEnum targetUnit = UnitEnum.B;
        double resultBytes = (double) bytes / targetUnit.getBytes();
        while (resultBytes >= 1024) {
            targetUnit = targetUnit.getNextUnit();
            resultBytes /= 1024;
        }
        double roundedResult = Math.round(resultBytes * 100) / 100.0; // 四舍五入保留两位小数
        return roundedResult + " " + targetUnit.name();
    }

    public static double convertKBToTargetUnit(double source,String targetUnit) {
        long bytes = (long) (source * UnitEnum.KB.getBytes());
        UnitEnum target = UnitEnum.getByUnit(targetUnit);
        double resultBytes = (double) bytes / target.getBytes();
        double roundedResult = Math.round(resultBytes * 100) / 100.0; // 四舍五入保留两位小数
        return roundedResult;
    }

    public static double convertBToTargetUnit(double source,String targetUnit) {
        long bytes = (long) (source * UnitEnum.B.getBytes());
        UnitEnum target = UnitEnum.getByUnit(targetUnit);
        double resultBytes = (double) bytes / target.getBytes();
        double roundedResult = Math.round(resultBytes * 100) / 100.0; // 四舍五入保留两位小数
        return roundedResult;
    }


    public static long convertSourceUnitToKB(double sourceData, String sourceUnit) {
        UnitEnum source = UnitEnum.getByUnit(sourceUnit);
        double sourceBytes = (double) (sourceData * source.getBytes());
        double resultBytes = (double) sourceBytes / UnitEnum.KB.getBytes();
        return  Math.round(resultBytes);
    }

    public static long convertSourceUnitToB(double sourceData, String sourceUnit) {
        UnitEnum source = UnitEnum.getByUnit(sourceUnit);
        double sourceBytes = (double) (sourceData * source.getBytes());
        double resultBytes = (double) sourceBytes / UnitEnum.B.getBytes();
        return  Math.round(resultBytes);
    }

    public static StorageUnit  convertStringToUnit(String source) {
        String regex = "(?<=\\d)(?=[a-zA-Z])|(?<=[a-zA-Z])(?=\\d)";

        String[] result = source.split(regex);
        StorageUnit storageUnit = StorageUnit.builder()
                .value(Double.valueOf(result[0]))
                .unit(result[1])
                .build();
        return storageUnit;
    }

    public static double convertSourceUnitToTargetUit(double source, String sourceUnit, String targetUnit){
        long sourceBytes = convertSourceUnitToB(source, sourceUnit);
        return convertBToTargetUnit(sourceBytes, targetUnit);
    }
}



enum UnitEnum {
    B(1L),
    KB(1024L),
    MB(1024L * 1024L),
    GB(1024L * 1024L * 1024L),
    TB(1024L * 1024L * 1024L * 1024L),
    PB(1024L * 1024L * 1024L * 1024L * 1024L);

    private long bytes;

    UnitEnum(long bytes) {
        this.bytes = bytes;
    }

    public long getBytes() {
        return bytes;
    }

    public UnitEnum getNextUnit() {
        int index = ordinal() + 1;
        if (index >= values().length) {
            return values()[values().length - 1];
        } else {
            return values()[index];
        }
    }

    public static UnitEnum getByUnit(String unit) {
        for (UnitEnum unitEnum : UnitEnum.values()) {
           if(unitEnum.name().equals(unit)){
               return unitEnum;
           }
        }
        return null;
    }
}

//1000单位制，看板用

