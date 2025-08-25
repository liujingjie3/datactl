package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** 任务详情信息 */
@Data
public class TaskDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long taskId;
    private String taskName;
    private String taskType;
    private String satellitesJson;
    private List<String> satellites;
    private LocalDateTime createTime;
    private Integer status;
    private String taskRequirement;
    private List<CurrentNodeVO> currentNodes;
    private List<TaskNodeVO> history;
    private List<TaskNodeVO> workflow;
}
