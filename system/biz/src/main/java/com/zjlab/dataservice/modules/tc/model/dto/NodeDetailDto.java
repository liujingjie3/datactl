package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.util.List;

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
    private Long roleId;
    private List<NodeActionDto> actions;
}
