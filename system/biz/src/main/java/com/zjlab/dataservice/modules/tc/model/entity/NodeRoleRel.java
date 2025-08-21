package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点与角色关联表
 */
@TableName("tc_node_role_rel")
@Data
public class NodeRoleRel extends TcBaseEntity implements Serializable {
    private static final long serialVersionUID = 6847330488062730480L;
    private Long nodeId;
    private String roleId;
}

