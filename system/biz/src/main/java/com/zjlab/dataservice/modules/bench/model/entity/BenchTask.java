package com.zjlab.dataservice.modules.bench.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("bench_tasks")
public class BenchTask {
    @TableId
    private Integer taskId;  // 主键ID

    /**
     * 任务名称（中文）
     */
    private String taskName;

    /**
     * 任务名称（英文）
     */
    private String taskNameEn;

    /**
     * 对应能力维度ID
     */
    private Integer capabilityId;

    /**
     * 任务标识符（简写）
     */
    private String taskIdentifier;

    /**
     * 评估指标
     */
    private String evaluationMetric;

//    private Integer tenantId;  // 租户ID
//    private Integer delFlag;  // 删除标志位: 0 未删除, 1 已删除
//
//    private String createBy;  // 创建人
//    @TableField(fill = FieldFill.INSERT)
//    private Date createTime;  // 创建时间
//
//    private String updateBy;  // 更新人
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private Date updateTime;  // 更新时间
}
