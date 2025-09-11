package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SatellitePassesDto {

    private String orbitNo;//圈次
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private String satelliteCode;//卫星代号
    private String groundStation;//地面站
    private String companyName;//地面站
    private String task;//地面站
    private Integer duration; //时长（秒）
    private Boolean used;
}
