package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 节点列表查询参数
 */
@Data
public class NodeQueryDto implements Serializable {
    private static final long serialVersionUID = 8207444442979298498L;
    private Integer status;
    private String name;
    private String roleId;

    private Integer page = 1;
    private Integer pageSize = 20;
}
