package com.zjlab.dataservice.modules.bench.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 模型表实体类
 */
@Data
@TableName("bench_models")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BenchAnalysisPo {
    // 模型名称
    private String name;

    // 团队ID
    private Integer teamId;

    // 发布日期
    private Date releaseDate;

    // 开闭源类型（枚举：开源 / 闭源）
    private String sourceType;

    // 模型类型
    private String modelType;

    // 参数量
    private String parameters;

    // 时空分析与推理能力
    private Float spatiotemporalReasoning;

    @TableField(exist = false)
    private String teamName; // 这里存 TeamEntity 的 name

    private Float pixChg;  // 像素变化

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer rank;

    // 综合得分
    @TableField(exist = false)
    private Float overallScore;

}
