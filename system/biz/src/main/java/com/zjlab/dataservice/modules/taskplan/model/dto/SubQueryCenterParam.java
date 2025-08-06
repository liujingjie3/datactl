package com.zjlab.dataservice.modules.taskplan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubQueryCenterParam {
    private String satelliteId;
    private LocalDateTime time;
}
