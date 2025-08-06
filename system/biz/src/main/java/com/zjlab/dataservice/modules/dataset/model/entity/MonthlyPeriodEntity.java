package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

@Data
public class MonthlyPeriodEntity {

    private String month;

    private Integer count;

    private String firstDay;
}
