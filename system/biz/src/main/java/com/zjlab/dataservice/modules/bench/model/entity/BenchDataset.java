package com.zjlab.dataservice.modules.bench.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;

@Data
@TableName("bench_datasets")
public class BenchDataset {
    @TableId
    private Integer id;  // 主键ID

    private String datasetName;  // 数据集名称
    private String datasetType;  // 数据集类型（分类/检测/分割等）

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String capabilityTags;  // 能力标签（JSON数组存储）

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String taskTags;  // 任务标签（JSON数组存储）

    private Integer dataSize;  // 数据集大小
    private String descriptionUrl;  // 描述文档URL
    private String gitUrl;  // Git/网站URL
    private String paperUrl;  // 论文URL
    private String descriptionMdUrl;  // 描述md 的地址

    private Integer tenantId;  // 租户ID
    private Integer delFlag;  // 删除标志位: 0 未删除, 1 已删除

    private String createBy;  // 创建人
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;  // 创建时间

    private String updateBy;  // 更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;  // 更新时间
}
