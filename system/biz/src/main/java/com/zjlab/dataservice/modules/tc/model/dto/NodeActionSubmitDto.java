package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 节点操作提交参数
 */
@Data
public class NodeActionSubmitDto implements Serializable {
    private static final long serialVersionUID = -2987980872321329065L;
    /** 任务ID */
    @NotNull
    private Long taskId;
    /** 节点实例ID */
    @NotNull
    private Long nodeInstId;
    /** 操作类型(0上传,1选择计划,2决策,3文本等) */
    @NotNull
    private Integer actionType;
    /** 提交内容JSON */
    private String actionPayload;
}
