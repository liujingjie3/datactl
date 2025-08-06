package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

@Data
public class WeeklyPeriodEntity {

    private String week;

    private Integer count;

    private String firstDay;
}
