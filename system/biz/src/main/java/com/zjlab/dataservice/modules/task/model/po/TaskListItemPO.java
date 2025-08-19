package com.zjlab.dataservice.modules.task.model.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务列表查询结果的持久对象
 */
@Data
public class TaskListItemPO {
    private Long taskId;
    private String taskName;
    private String taskCode;
    /** 模板对象JSON */
    private String template;
    /** 卫星JSON */
    private String satellites;
    private LocalDateTime createTime;
    private Integer status;
    /** 当前节点数组JSON */
    private String currentNodes;
}
