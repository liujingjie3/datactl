package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体，包含公共字段
 */
@Data
public class TcBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @TableLogic(value = "0", delval = "1")
    private Integer delFlag; // 0=未删, 1=已删

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}

