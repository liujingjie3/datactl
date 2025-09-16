package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 节点角色下的处理人信息
 */
@Data
public class NodeRoleUserDto implements Serializable {
    private static final long serialVersionUID = -5097686844374189897L;

    /** 处理人用户ID */
    private String userId;

    /** 处理人姓名 */
    private String realName;
}

