package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务编辑时的任务基本信息
 */
@Data
public class TaskManagerEditInfo implements Serializable {

    private static final long serialVersionUID = 5250567804316528322L;
    /** 任务状态 */
    private Integer status;
    /** 发起人 */
    private String createBy;
    /** 模板ID */
    private String templateId;
    /** 是否展示结果 */
    private Integer resultDisplayNeeded;
}
