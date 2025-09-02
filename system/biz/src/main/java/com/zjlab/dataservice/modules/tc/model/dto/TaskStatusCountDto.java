package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

/**
 * 状态任务数量
 */
@Data
public class TaskStatusCountDto {
    /** 任务状态 */
    private Integer status;
    /** 数量 */
    private Long count;
}

