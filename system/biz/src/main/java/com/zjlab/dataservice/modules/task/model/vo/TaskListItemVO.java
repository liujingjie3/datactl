package com.zjlab.dataservice.modules.task.model.vo;

import com.zjlab.dataservice.modules.task.enums.TaskStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 任务列表的返回对象 */
@Data
public class TaskListItemVO {
    private Long taskId;
    private String taskName;
    private String taskCode;
    private TemplateVO template;
    private String satellites;
    private LocalDateTime createTime;
    private TaskStatusEnum status;
    private List<CurrentNodeVO> currentNodes;
}
