package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data

public class SatellitePassesDto {

    private Integer id;//圈次
    private LocalDateTime aos;
    private LocalDateTime los;

    private String sat;//卫星代号
    private String station;//地面站

    private Long duration; //时长（秒）

    private Boolean delFlag;
}
