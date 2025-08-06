package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class DomesticWeatherInfoDay implements IWeatherInfoDay{
    private String fc_time;
    private String week;

    private String wp_day_code;
    private String wp_day;
    private String wp_night_code;
    private String wp_night;

    private int tem_max;
    private int tem_min;
    private int real_tem_max;
    private int real_tem_min;  // ✅ 确保是 int，不是 String！

    private String ws_day;
    private String wd_day;
    private String ws_night;
    private String wd_night;

    private int rh_max;
    private int rh_min;

    private double prs;

    private int pre_pro_day;
    private double pre_day;
    private int pre_pro_night;
    private double pre_night;

    private String sunrise;
    private String sunset;

    private int uv_level;
    private String uv_desc;

    private int vis;
}