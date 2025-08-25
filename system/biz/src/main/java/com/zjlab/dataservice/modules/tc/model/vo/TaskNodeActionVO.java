package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 节点操作记录信息 */
@Data
public class TaskNodeActionVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 操作时间 */
    private LocalDateTime actionTime;
    /** 操作人ID */
    private String operatorId;
    /** 操作人名称 */
    private String operatorName;
    /** 操作类型 */
    private Integer actionType;
    /** 提交内容JSON */
    private String actionPayload;
}
