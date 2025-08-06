package com.zjlab.dataservice.modules.screen.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "regional_statistics")
public class RegionalStatisticsPo {

    private Integer id;

    private String regionCode;

    private Double regionArea;

    private Double coverageArea;

    private Double coverageRate;

    private Double overlapArea;

    private Double totalArea;

    private Double overlapRate;

    private String imageType;

    private String year;

    private Boolean deleted;

    private String createBy;

    private Timestamp createTime;

    private String updateBy;

    private Timestamp updateTime;

}
