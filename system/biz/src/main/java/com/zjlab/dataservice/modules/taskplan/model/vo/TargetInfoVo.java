package com.zjlab.dataservice.modules.taskplan.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data

public class TargetInfoVo {
    private String satelliteId;
    private String satelliteName;
    private double  camPeriod;
    private long camStartTime;
    private long camEndTime;
    private String camCenterLa;
    private String camCenterLo;
}
