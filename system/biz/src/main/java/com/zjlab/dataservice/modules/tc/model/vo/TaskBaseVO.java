package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务通用返回字段
 */
@Data
public class TaskBaseVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long taskId;
    private String taskName;
    private LocalDateTime createTime;
    private Integer status;
    private List<CurrentNodeVO> currentNodes;
}
