package com.zjlab.dataservice.modules.bench.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
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
public class BenchModelPo {
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

    @TableField(exist = false)
    private Integer rank;


    // 时空分析与推理能力
    private Float spatiotemporalReasoning;

    @TableField(exist = false)
    private String teamName; // 这里存 TeamEntity 的 name

}
