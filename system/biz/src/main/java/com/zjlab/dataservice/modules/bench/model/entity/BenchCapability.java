package com.zjlab.dataservice.modules.bench.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 能力维度表（bench_capabilities）
 */
@Data
@TableName("bench_capabilities")
public class BenchCapability {
    @TableId
    private Integer id;  // 主键 ID

    private String name;  // 维度名称
    private String nameEn; // 维度英文名称

    @TableField(value = "ability_identifier")
    private String abilityIdentifier; // 能力维度标注

    private String description; // 维度描述
    private Integer tenantId;  // 租户ID
    private Integer delFlag;  // 删除标志位:0未删除;1已删除

    private String createBy;  // 创建人
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;  // 创建时间

    private String updateBy;  // 更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;  // 更新时间
}
