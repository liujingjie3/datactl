package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 任务列表的返回对象 */
@Data
public class TaskManagerListItemVO {
    private Long taskId;
    private String taskName;
    private String taskCode;
    private Long templateId;
    private String templateName;
    private String satellites;
    private LocalDateTime createTime;
    private Integer status;
    private List<CurrentNodeVO> currentNodes;
}
