package com.zjlab.dataservice.modules.tc.model.vo;

import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** 节点历史信息 */
@Data
public class TaskHistoryNodeVO implements Serializable {

    private static final long serialVersionUID = -6904767033716008113L;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 节点名称 */
    private String nodeName;
    /** 层级序号 */
    private Integer orderNo;
    /** 节点状态 */
    private Integer status;
    /** 处理时间 */
    private LocalDateTime processTime;
    /** 操作人名称 */
    private String operatorName;
    /** 当前节点待处理角色 */
    private List<NodeRoleDto> roles;
    /** 操作日志 */
    private List<TaskNodeActionVO> actionLogs;
}
