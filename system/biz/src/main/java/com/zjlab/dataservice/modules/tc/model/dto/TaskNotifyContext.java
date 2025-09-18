package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知使用的任务上下文
 */
@Data
public class TaskNotifyContext implements Serializable {

    private static final long serialVersionUID = -221895068756980509L;

    private Long taskId;
    private String taskName;
    private String creatorId;
    private LocalDateTime createTime;
}
