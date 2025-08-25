package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO 基础类，包含通用的创建及更新字段
 */
@Data
public class TcBaseEntityDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    /** 创建人ID */
    private String creatorId;
    /** 创建人名称 */
    private String creatorName;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新人ID */
    private String updaterId;
    /** 更新人名称 */
    private String updaterName;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
