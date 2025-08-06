package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "TodoInstanceUpdateVO对象", description = "任务流程实例更新VO对象")
public class TodoInstanceUpdateDto implements Serializable {
    private static final long serialVersionUID = -8947154435374683742L;
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String corpId;

    @NotBlank(message = "instance_code不能为空")
    @ApiModelProperty(value = "实例编码")
    private String code;

    @NotNull(message = "flag不能为空")
    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    private Integer flag;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1取消")
    private Integer result;
}
