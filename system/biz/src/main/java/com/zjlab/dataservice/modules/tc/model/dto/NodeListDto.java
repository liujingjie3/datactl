package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 节点列表返回
 */
@Data
public class NodeListDto {
    private Long id;
    private String name;
    private String description;
    private Integer expectedDuration;
    private Integer timeoutRemind;
    private Integer status;
    private Long roleId;
    private String roleName;
    private List<String> actionTypes;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
}
