package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "TaskBaseVO对象", description = "待办查询对象")
public class BaseTaskDto implements Serializable {
    private static final long serialVersionUID = 946697728067685180L;

    private String taskId;

    @ApiModelProperty(value = "待办任务编码")
    private String taskCode;
    /**
     * 0.运行,1完成，-1.取消
     */
    @NotNull(message = "flag不能为空")
    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    @Range(min = -1, max = 1, message = "flag不合法取值")
    private Integer flag;

    /**
     * 1同意，-1拒绝
     */
    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1取消")
    private Integer result;

    @ApiModelProperty(value = "办理意见")
    private String opinion;

}
