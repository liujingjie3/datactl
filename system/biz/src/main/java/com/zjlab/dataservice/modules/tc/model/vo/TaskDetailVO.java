package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.util.List;

/** 任务详情信息 */
@Data
public class TaskDetailVO extends TaskBaseVO {
    private static final long serialVersionUID = 1L;
    private String taskType;
    private String satellitesJson;
    private List<String> satellites;
    private String taskRequirement;
    private List<TaskNodeVO> history;
    private List<TaskNodeVO> workflow;
}
