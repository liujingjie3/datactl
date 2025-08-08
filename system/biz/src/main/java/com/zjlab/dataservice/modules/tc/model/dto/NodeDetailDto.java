package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.util.List;


import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;

/**
 * 节点详情
 */
@Data
public class NodeDetailDto {
    private Long id;
    private String name;
    private String description;
    private Integer expectedDuration;
    private Integer timeoutRemind;
    private Integer status;

    private List<NodeRoleDto> roles;

    private List<NodeActionDto> actions;
}
