package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询当前节点的中间对象
 */
@Data
public class CurrentNodeRow implements Serializable {
    private static final long serialVersionUID = -6880543914278197328L;
    private Long taskId;
    private Long nodeInstId;
    private String nodeName;
    /**
     * 节点处理角色ID列表(JSON字符串)
     */
    private String handlerRoleIds;
}

