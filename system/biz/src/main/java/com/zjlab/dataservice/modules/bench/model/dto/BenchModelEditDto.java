package com.zjlab.dataservice.modules.bench.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * BenchModel 编辑 DTO
 */
@Data
public class BenchModelEditDto {
    private Integer id;

    private String name;

    // 团队ID
    private Integer teamId;

    // 发布日期
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate releaseDate;

    // 开闭源类型（枚举：开源 / 闭源）
    private String sourceType;

    // 模型类型
    private String modelType;

    // 参数量
    private String parameters;

    private Integer count;

    // 综合得分
    private Float overallScore;

    // 粗粒度视觉感知能力
    private Float globalVisualPerception;

    // 细粒度视觉感知能力
    private Float fineVisualPerception;

    // 视觉问答能力
//    private Float visualQuestionAnswering;

    // 语言生成能力
    private Float languageGeneration;

    private LocalDateTime updateTime;

    private String teamName;

    private Integer rank;


}
