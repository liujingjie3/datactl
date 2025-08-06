package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

@Data
public class ForeignWeatherInfoDay implements IWeatherInfoDay{
    private String fc_time;
    private String week;

    private String wp_code;
    private String wp;

    private String ws_desc;  // 风力
    private String wd_desc;  // 风向

    private int tem_max;         // 最高温度
    private int tem_min;         // 最低温度
    private int real_tem_max;    // 最高体感温度
    private int real_tem_min;    // 最低体感温度

    private double prs;          // 地面气压
    private double pre;          // 降水量
    private int pre_pro;         // 降水概率

    private int rh;              // 相对湿度

    private int cloud_cover;     // 云量

    private String sunrise;
    private String sunset;

    private int uv_level;
    private String uv_desc;

    private String moonrise;
    private String moonset;
    private String moonphase;
}