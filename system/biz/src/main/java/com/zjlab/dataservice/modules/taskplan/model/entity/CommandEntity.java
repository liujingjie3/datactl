package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class CommandEntity {
    private String satelliteId;
    private int count;
    private long startTimestamp;
    private long endTimestamp;
    private double swe;
    private double swn;
    private double nee;
    private double nen;
    private double camPeriod;
    private double camCenterLa;
    private double camCenterLo;
    private double offNadirDeg;
}
