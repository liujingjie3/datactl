package com.zjlab.dataservice.modules.tc.model.vo;

import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** 当前激活节点信息 */
@Data
public class CurrentNodeVO implements Serializable {
    private static final long serialVersionUID = -1419598682385683115L;
    private Long nodeInstId;
    private String nodeName;
    private List<NodeRoleDto> roles;
}
