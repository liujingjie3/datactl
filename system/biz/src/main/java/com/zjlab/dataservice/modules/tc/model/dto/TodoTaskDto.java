package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "TodoTaskVO对象", description = "待办任务信息表")
public class TodoTaskDto  implements Serializable {

    private static final long serialVersionUID = 8072893478627824959L;

    @ApiModelProperty(value = "待办任务ID")
    private String taskId;

    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String corpId;

    @NotBlank(message = "instance_code不能为空")
    @ApiModelProperty(value = "实例编码")
    private String instanceCode;

    @NotBlank(message = "task_code不能为空")
    @ApiModelProperty(value = "待办任务编码")
    private String code;

    @ApiModelProperty(value = "待办人id")
    @NotBlank(message = "to_user_id不能为空")
    private String toUserId;

    /**
     * 待办发起时间
     */
    @ApiModelProperty(value = "待办发起时间戳")
    @NotNull(message = "create_on不能为空")
    private Long createOn;

    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;

    @ApiModelProperty(value = "h5页面跳转url")
    private String h5Url;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    private Integer flag;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1取消")
    private Integer result;

}
