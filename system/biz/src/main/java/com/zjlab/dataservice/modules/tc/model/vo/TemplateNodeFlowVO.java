package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 模板节点流信息
 */
@Data
public class TemplateNodeFlowVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点名称 */
    private String nodeName;

    /** 节点描述 */
    private String nodeDescription;

    /** 节点处理人姓名，逗号分隔 */
    private String handlerRealName;

    /** 节点处理时长（分钟） */
    private Integer maxDuration;
}
