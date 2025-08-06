package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatisticsEntity {
    private Integer id;
    private String name_cn;
    private String name_en;
    private Double totalArea;
    private Integer count;
    private Double percent;

}
