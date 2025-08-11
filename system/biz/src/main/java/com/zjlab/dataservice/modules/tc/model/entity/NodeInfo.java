package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 节点信息表
 */
@TableName("tc_node_info")
@Data
public class NodeInfo extends TcBaseEntity {
    private String name;
    private String description;
    private Integer expectedDuration;
    private Integer timeoutRemind;
    /** 节点状态（1=活跃，0=禁用） */
    private Integer status;
    /** 操作控制项数组：[{type,name,config}] */
    private String actions;
}

