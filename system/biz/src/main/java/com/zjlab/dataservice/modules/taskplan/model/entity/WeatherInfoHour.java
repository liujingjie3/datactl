package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class WeatherInfoHour {
    private String fc_time;
    private double tem;
    private double real_tem;
    private double dp_tem;
    private double ws;
    private String ws_desc;
    private double wd;
    private String wd_desc;
    private int rh;
    private double prs;
    private int vis;
    private int cloud_cover;
    private double pre;
    private String wp_code;
    private String wp;
}
