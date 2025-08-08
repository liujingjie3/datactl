package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.util.List;

import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;

/**
 * 新建或编辑节点请求
 */
@Data
public class NodeCreateOrUpdateDto {
    /** 节点名称 */
    private String name;
    /** 节点描述 */
    private String description;
    /** 预计处理时长（分钟） */
    private Integer expectedDuration;
    /** 超时提醒频率（分钟） */
    private Integer timeoutRemind;
    /** 节点状态（1=活跃，0=禁用） */
    private Integer status;
    /** 节点角色列表 */
    private List<NodeRoleDto> roles;
    /** 绑定的操作控制项 */
    private List<NodeActionDto> actions;
}
