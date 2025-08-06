package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zjlab.dataservice.common.api.page.PageRequest;

@Data
@ApiModel(value = "BasePage对象", description = "分页查询对象")
public class BaseTcDto extends PageRequest  {
    private static final long serialVersionUID = -6597049162526033554L;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 当前登录用户标识(工号)
     */
    private String userCode;

    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    private Integer flag;

    private String typeCode;

    @ApiModelProperty(value = "查询起始时间")
    private String startTime;

    @ApiModelProperty(value = "查询结束时间")
    private String endTime;

}
