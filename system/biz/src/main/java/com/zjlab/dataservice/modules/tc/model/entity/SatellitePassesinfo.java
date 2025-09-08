package com.zjlab.dataservice.modules.tc.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class SatellitePassesinfo {

    private Integer id;//圈次
    private LocalDateTime aos;
    private LocalDateTime los;

    private String sat;//卫星代号
    private String station;//地面站

    private Integer duration; //时长（秒）


}
