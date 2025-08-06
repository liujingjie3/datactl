package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "TodoReadUpdateVO对象", description = "待阅任务更新VO对象")
public class TodoReadUpdateDto implements Serializable {

    private static final long serialVersionUID = -2763472341069212568L;
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String corpId;

    @NotBlank(message = "instance_code不能为空")
    @ApiModelProperty(value = "实例编码")
    private String instanceCode;

    @Valid
    @ApiModelProperty(value = "BaseReadDto对象")
    private List<BaseReadDto> reads;

}
