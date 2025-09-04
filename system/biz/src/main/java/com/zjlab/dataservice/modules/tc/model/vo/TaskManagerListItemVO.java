package com.zjlab.dataservice.modules.tc.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/** 任务列表的返回对象 */
@Data
public class TaskManagerListItemVO extends TaskBaseVO {
    private static final long serialVersionUID = -3510477925167338638L;
    private String taskCode;
    private String templateId;
    private String templateName;
    private String taskRequirement;
    private Integer resultDisplayNeeded;
    private List<SatelliteGroupVO> satellites;
    @JsonIgnore
    private String satellitesJson;
}
