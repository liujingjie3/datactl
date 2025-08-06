package com.zjlab.dataservice.modules.bench.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据集-任务映射表（含评分）实体类，映射数据库中的 `bench_score` 表
 */
@Data
@TableName("bench_score")
public class BenchScore {

    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 模型ID
     */
    private Integer modelId;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * 任务标识符（简写）
     */
    private String taskIdentifier;


    /**
     * 计算指标，例如 Accuracy, mIoU, CIDEr, AP@0.5 等
     */
    private String metric;

    /**
     * 得分
     */
    private double score;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdated;

    public BenchScore(String datasetName, double score, Integer modelId, String taskIdentifier, String metric, LocalDateTime lastUpdated) {
        this.datasetName = datasetName;
        this.score = score;
        this.modelId = modelId;
        this.taskIdentifier = taskIdentifier;
        this.metric = metric;
        this.lastUpdated = lastUpdated;
    }

}
