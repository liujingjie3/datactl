package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务数量统计
 */
@Data
public class TaskCountVO  implements Serializable {
    private static final long serialVersionUID = -5978063147559715091L;
    /** 总任务数 */
    private Long total;
    /** 运行中任务数 */
    private Long running;
    /** 已完成任务数 */
    private Long completed;
    /** 已取消任务数 */
    private Long canceled;
}

