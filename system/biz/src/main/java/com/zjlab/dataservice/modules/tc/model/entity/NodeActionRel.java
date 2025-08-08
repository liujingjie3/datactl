package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 节点与操作控制项关联表
 */
@TableName("node_action_rel")
@Data
public class NodeActionRel extends TcBaseEntity {
    private Long nodeId;
    private Long actionId;
}

