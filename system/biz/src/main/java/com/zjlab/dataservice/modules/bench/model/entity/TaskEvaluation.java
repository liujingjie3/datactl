package com.zjlab.dataservice.modules.bench.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务调和平均得分表实体类，映射数据库中的 `bench_task_evaluation` 表
 */
@Data
@TableName("bench_task_evaluation")
public class TaskEvaluation {

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
     * 任务标识符，
     */
    private String taskIdentifier;

    /**
     * 任务中文名称，如 图像分类
     */
    private String taskName;

    /**
     * 该任务适用的数据集数量
     */
    private Integer datasetCount;

    /**
     * 该任务在不同数据集上的调和平均得分
     */
    private Float harmonicMeanScore;

    /**
     * 任务对应的评估指标，如 Accuracy, mIoU
     */
    private String evaluationMetric;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdated;

    @TableField(exist = false)
    private Integer capabilityId;

}
