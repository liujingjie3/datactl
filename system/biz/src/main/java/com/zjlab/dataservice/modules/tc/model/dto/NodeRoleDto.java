package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 节点角色
 */
@Data
public class NodeRoleDto implements Serializable {
    private static final long serialVersionUID = -3919676706141964648L;
    private String roleId;
    private String roleName;
    private List<NodeRoleUserDto> users;
}

