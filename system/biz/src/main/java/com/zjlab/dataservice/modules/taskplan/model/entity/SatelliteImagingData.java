package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class SatelliteImagingData {

    /** 卫星编号 */
    private String satelliteId;

    /** 预计影像数量 */
    private Integer count;

    /** 起始时间戳（毫秒） */
    private Long startTimestamp;

    /** 结束时间戳（毫秒） */
    private Long endTimestamp;

    /** 成像区域西南经度 */
    private Double swe;

    /** 成像区域西南纬度 */
    private Double swn;

    /** 成像区域东北经度 */
    private Double nee;

    /** 成像区域东北纬度 */
    private Double nen;

    /** 拍摄时长（单位：秒或分钟，根据你的业务定义） */
    private Double camPeriod;

    /** 拍摄中心经度 */
    private String camCenterLa;

    /** 拍摄中心纬度 */
    private String camCenterLo;

    /** 侧摆角度 */
    private Double offNadirDeg;

    /** 升降轨 */
     private Integer elevator;;
}

