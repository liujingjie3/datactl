package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class WeatherInfo {
    private String cloudCover;
    private String wind;     // 风速描述
    private double pre;      // 降水 mm
    private int tem;         // 温度 ℃
    private int rh;          // 湿度 %
    private double prs;      // 气压 hPa
}

