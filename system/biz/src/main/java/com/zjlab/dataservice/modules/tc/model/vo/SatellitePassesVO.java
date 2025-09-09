package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class SatellitePassesVO {

    private String orbitNo;//圈次
    private LocalDateTime inTime;
    private LocalDateTime outTime;

    private String satelliteCode;//卫星代号
    private String groundStation;//地面站

    private Long duration; //时长（秒）
}
