package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.modules.tc.model.vo.TaskNodeActionVO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 节点操作提交参数
 */
@Data
public class NodeActionSubmitDto implements Serializable {
    private static final long serialVersionUID = -2987980872321329065L;
    /** 任务ID */
    @NotNull
    private Long taskId;
    /** 节点实例ID */
    @NotNull
    private Long nodeInstId;
    /** 操作列表 */
    @NotEmpty
    private List<TaskNodeActionVO> actions;
}
