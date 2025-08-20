package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

/**
 * 查询当前节点的中间对象
 */
@Data
public class CurrentNodeRow {
    private Long taskId;
    private Long nodeInstId;
    private String nodeName;
    /**
     * 节点处理角色ID列表(JSON字符串)
     */
    private String handlerRoleIds;
}

