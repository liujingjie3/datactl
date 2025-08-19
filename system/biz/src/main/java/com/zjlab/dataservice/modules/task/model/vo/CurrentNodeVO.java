package com.zjlab.dataservice.modules.task.model.vo;

import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import lombok.Data;

import java.util.List;

/** 当前激活节点信息 */
@Data
public class CurrentNodeVO {
    private Long nodeInstId;
    private String nodeName;
    private List<NodeRoleDto> roles;
}
