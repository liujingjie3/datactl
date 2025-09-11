package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 任务保存的公共字段
 */
@Data
public class TaskManagerBaseDto implements Serializable {


    private static final long serialVersionUID = -6867405314065688881L;
    /** 任务名称 */
    @NotBlank(message = "taskName不能为空")
    private String taskName;
    /** 任务需求 */
    private String taskRequirement;
    /** 是否成像(0否,1是) */
    @NotNull(message = "needImaging不能为空")
    private Integer needImaging;
    /** 成像区域ID */
    private Long imagingAreaId;
    /** 是否展示结果(0否,1是) */
    @NotNull(message = "resultDisplayNeeded不能为空")
    private Integer resultDisplayNeeded;
}
