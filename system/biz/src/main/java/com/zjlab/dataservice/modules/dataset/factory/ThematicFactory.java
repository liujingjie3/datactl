package com.zjlab.dataservice.modules.dataset.factory;

public class ThematicFactory {
    public static String getTableNameByType(Integer pac) {
        switch (pac / 10000) {
            case 32:
                return "thematic_jiangsu";
            case 31:
                return "thematic_shanghai";
            case 33:
                return "thematic_zhejiang";
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}
