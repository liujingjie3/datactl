package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;

/** 节点操作记录信息 */
@Data
public class TaskNodeActionVO implements Serializable {

    private static final long serialVersionUID = 8431966549122817451L;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 操作类型 */
    private Integer actionType;
    /** 提交内容JSON */
    private String actionPayload;
}
