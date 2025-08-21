package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

/**
 * 节点列表查询参数
 */
@Data
public class NodeQueryDto {
    private Integer status;
    private String name;
    private String roleId;

    private Integer page = 1;
    private Integer pageSize = 20;
}
