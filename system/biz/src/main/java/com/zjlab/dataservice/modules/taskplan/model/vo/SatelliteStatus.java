package com.zjlab.dataservice.modules.taskplan.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class SatelliteStatus {
    private String id;
    private String name;
    private boolean exists;
}