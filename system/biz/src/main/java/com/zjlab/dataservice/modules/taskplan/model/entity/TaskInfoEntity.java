package com.zjlab.dataservice.modules.taskplan.model.entity;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskInfoEntity extends PageRequest {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String geom;

    private Integer status;
}
