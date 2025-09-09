package com.zjlab.dataservice.modules.tc.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;


/** 任务详情信息 */
@Data
public class TaskDetailVO extends TaskBaseVO {

    private static final long serialVersionUID = 6353472493151876597L;
    private String taskType;
    private List<SatelliteGroupVO> satellites;
    @JsonIgnore
    private String satellitesJson;
    private List<RemoteCmdExportVO> remoteCmds;
    @JsonIgnore
    private String remoteCmdsJson;
    private List<OrbitPlanExportVO> orbitPlans;
    @JsonIgnore
    private String orbitPlansJson;
    private String taskRequirement;
    private String imagingArea;
    private List<TaskHistoryNodeVO> history;
    private List<TaskWorkflowNodeVO> workflow;
}
