package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 任务创建入参
 */
@Data
public class TaskManagerCreateDto extends TaskManagerBaseDto {


    private static final long serialVersionUID = 8407968386506820592L;
    /** 模板ID */
    @NotBlank(message = "templateId不能为空")
    private String templateId;
    /** 执行卫星(JSON) */
    private String satellites;
    /** 遥控指令单(JSON) */
    private String remoteCmds;
    /** 轨道计划(JSON) */
    private String orbitPlans;
}
