package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** 工作流节点展示信息 */
@Data
public class TaskWorkflowNodeVO implements Serializable {

    private static final long serialVersionUID = 3019261426232449059L;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 节点名称 */
    private String nodeName;
    /** 层级序号 */
    private Integer orderNo;
    /** 节点状态 */
    private Integer status;
    /** 前驱节点 */
    private List<Long> prevNodeIds;
    /** 后继节点 */
    private List<Long> nextNodeIds;
}
