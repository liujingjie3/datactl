package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 状态任务数量
 */
@Data
public class TaskStatusCountDto  implements Serializable {
    private static final long serialVersionUID = -2857439168225563040L;
    /** 任务状态 */
    private Integer status;
    /** 数量 */
    private Long count;
}

