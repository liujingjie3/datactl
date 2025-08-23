package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 任务创建入参
 */
@Data
public class TaskManagerCreateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 任务名称 */
    @NotBlank(message = "taskName不能为空")
    private String taskName;
    /** 任务需求 */
    private String taskRequirement;
    /** 模板ID */
    @NotBlank(message = "templateId不能为空")
    private String templateId;
    /** 执行卫星(JSON) */
    private String satellites;
    /** 是否成像(0否,1是) */
    @NotNull(message = "needImaging不能为空")
    private Integer needImaging;
    /** 成像区域(JSON) */
    private String imagingArea;
    /** 是否展示结果(0否,1是) */
    @NotNull(message = "resultDisplayNeeded不能为空")
    private Integer resultDisplayNeeded;
    /** 遥控指令单(JSON) */
    private String remoteCmds;
    /** 轨道计划(JSON) */
    private String orbitPlans;
}
