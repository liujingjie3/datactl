package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "ReadBaseVO对象", description = "待阅查询对象")
public class BaseReadDto implements Serializable {

    private static final long serialVersionUID = -8330315384230229179L;
    @ApiModelProperty(value = "待阅任务编码")
    @NotBlank(message = "read_code不能为空")
    private String readCode;

    @ApiModelProperty(value = "待阅任务ID")
    private String readId;

    /**
     * 0.运行,1已阅，-1取消
     */
    @NotNull(message = "flag不能为空")
    @Range(min = 0, max = 1, message = "flag不合法的参数值")
    @ApiModelProperty(value = "状态标识，0待阅，1已阅，-1取消")
    private Integer flag;
}
