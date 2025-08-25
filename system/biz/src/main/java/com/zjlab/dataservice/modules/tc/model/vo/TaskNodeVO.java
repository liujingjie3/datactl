package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** 工作流节点信息 */
@Data
public class TaskNodeVO implements Serializable {

    private static final long serialVersionUID = -572132768269317377L;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 节点名称 */
    private String nodeName;
    /** 层级序号 */
    private Integer orderNo;
    /** 节点状态 */
    private Integer status;
    /** 开始时间 */
    private LocalDateTime startedAt;
    /** 完成时间 */
    private LocalDateTime completedAt;
    /** 完成操作人ID */
    private String completedBy;
    /** 处理时间 */
    private LocalDateTime processTime;
    /** 操作人ID */
    private String operatorId;
    /** 操作人名称 */
    private String operatorName;
    /** 操作日志 */
    private List<TaskNodeActionVO> actionLogs;
}
