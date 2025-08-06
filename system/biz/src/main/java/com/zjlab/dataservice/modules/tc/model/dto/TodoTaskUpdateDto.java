package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "TodoTaskUpdateVO对象", description = "待办任务更新VO对象")
public class TodoTaskUpdateDto implements Serializable {

    private static final long serialVersionUID = -2763472341069212568L;
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String corpId;

    @NotBlank(message = "instance_code不能为空")
    @ApiModelProperty(value = "实例编码")
    private String instanceCode;

    @Valid
    @ApiModelProperty(value = "TaskBaseVO对象")
    private List<BaseTaskDto> tasks;

}
