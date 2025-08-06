package com.zjlab.dataservice.modules.bench.model.dto;

import lombok.Data;

/**
 * BenchModel 添加 DTO
 */
@Data
public class BenchModelAddDto {
    private String name;
    private Integer teamId;
    private String sourceType;
    private String modelType;
    private Long parameters;
    private Float overallScore;
}
