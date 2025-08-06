package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tc_todo_task")
@ApiModel(value = "TodoTask对象", description = "待办任务信息表")
public class TodoTask extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    /**
     * 待办任务编码
     */
    @ApiModelProperty(value = "待办任务编码")
    private String code;
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例编码")
    private String instanceCode;

    @ApiModelProperty(value = "实例ID")
    private String instanceId;
    /**
     * 待办人id
     */
    @ApiModelProperty(value = "待办人id")
    private String toUserId;

    /**
     * 待阅发起时间
     */
    @ApiModelProperty(value = "发起时间")
    private Long createOn;
    /**
     * 标识， -1取消，0运行中，1完成
     */
    @ApiModelProperty(value = "标识")
    private Integer flag;
    /**
     * 结果标识，-1拒绝，0运行中，1同意
     */
    @ApiModelProperty(value = "结果标识")
    private Integer result;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * pc跳转url
     */
    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;
    /**
     * h5页面跳转url
     */
    @ApiModelProperty(value = "h5页面跳转url")
    private String h5Url;

    /**
     * 客户端标识
     */
    @ApiModelProperty(value = "客户端标识")
    private String corpId;
    /**
     * 客户端应用标识
     */
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;
    /**
     * 是否纠正， 0否 1是
     */
    @ApiModelProperty(value = "是否纠正")
    private Integer urge;
    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段")
    private String ext;
    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime gmtAudit;
    /**
     * 审核者
     */
    @ApiModelProperty(value = "审核者")
    private String gmtAuditBy;
    /**
     * 办理意见
     */
    @ApiModelProperty(value = "办理意见")
    private String opinion;

}
