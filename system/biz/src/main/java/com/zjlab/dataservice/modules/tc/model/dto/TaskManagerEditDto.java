package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务编辑入参
 */
@Data
public class TaskManagerEditDto extends TaskManagerBaseDto {


    private static final long serialVersionUID = 2245295950382672740L;
    /** 任务ID */
    @NotNull(message = "taskId不能为空")
    private Long taskId;
}
