package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "TodoReadVO对象", description = "待阅任务信息表")
public class TodoReadDto extends PageRequest implements Serializable {
    private static final long serialVersionUID = -7885575406813260125L;

    @ApiModelProperty(value = "待阅ID")
    private String readId;

    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String corpId;

    @ApiModelProperty(value = "实例编码")
    private String instanceCode;

    @ApiModelProperty(value = "待阅编码")
    private String code;

    @ApiModelProperty(value = "待阅人ID")
    private String toUserId;

    /**
     * 待阅发起时间
     */
    @ApiModelProperty(value = "待阅发起时间戳")
    private Long createOn;

    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;

    @ApiModelProperty(value = "h5页面跳转url")
    private String h5Url;

    @ApiModelProperty(value = "状态标识，0待阅，1已阅，-1取消")
    private Integer flag;
}
