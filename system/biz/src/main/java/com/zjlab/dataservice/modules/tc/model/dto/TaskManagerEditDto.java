package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 任务编辑入参
 */
@Data
public class TaskManagerEditDto implements Serializable {


    private static final long serialVersionUID = 2245295950382672740L;
    /** 任务ID */
    @NotNull(message = "taskId不能为空")
    private Long taskId;
    /** 任务名称 */
    @NotBlank(message = "taskName不能为空")
    private String taskName;
    /** 任务需求 */
    private String taskRequirement;
    /** 是否展示结果(0否,1是) */
    @NotNull(message = "resultDisplayNeeded不能为空")
    private Integer resultDisplayNeeded;
}
