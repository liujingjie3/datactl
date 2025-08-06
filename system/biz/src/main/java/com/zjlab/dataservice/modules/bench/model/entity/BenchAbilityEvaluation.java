package com.zjlab.dataservice.modules.bench.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 能力维度调和平均得分表实体类，映射数据库中的 `bench_ability_evaluation` 表
 */
@Data
@TableName("bench_ability_evaluation")
public class BenchAbilityEvaluation {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 模型ID
     */
    private Integer modelId;

    /**
     * 能力标识符，如 coarse_visual_perception
     */
    private String abilityIdentifier;

    /**
     * 能力中文名称，如 全局视觉感知能力
     */
    private String abilityName;

    /**
     * 该能力维度下的任务数量
     */
    private Integer taskCount;

    /**
     * 该能力维度下任务得分的调和平均数
     */
    private Float harmonicMeanScore;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdated;

    /**
     * 租户ID
     */
    private Byte tenantId;

    /**
     * 删除标志位: 0未删除; 1已删除
     */
    private Byte delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
