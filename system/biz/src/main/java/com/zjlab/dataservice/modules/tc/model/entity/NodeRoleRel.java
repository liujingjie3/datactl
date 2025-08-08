package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 节点与角色关联表
 */
@TableName("tc_node_role_rel")
@Data
public class NodeRoleRel extends TcBaseEntity {
    private Long nodeId;
    private Long roleId;
}

