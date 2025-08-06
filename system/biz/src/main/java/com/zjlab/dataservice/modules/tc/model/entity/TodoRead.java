package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tc_todo_read")
@ApiModel(value = "TodoRead对象", description = "待阅任务信息表")
public class TodoRead extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String readId;
    /**
     * 待办任务编码
     */
    @ApiModelProperty(value = "待办任务编码")
    private String code;
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例code")
    private String instanceCode;

    @ApiModelProperty(value = "实例id")
    private String instanceId;
    /**
     * 待办人id
     */
    @ApiModelProperty(value = "待办人id")
    private String toUserId;

    /**
     * 待办发起时间
     */
    @ApiModelProperty(value = "待办发起时间")
    private Long createOn;
    /**
     * 标识，-1取消，0运行中，1完成
     */
    @ApiModelProperty(value = "标识")
    private Integer flag;
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
     * 是否纠正
     */
    @ApiModelProperty(value = "是否纠正")
    private Integer urge;
    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段")
    private String ext;
    /**
     * 审阅时间
     */
    @ApiModelProperty(value = "审阅时间")
    private LocalDateTime gmtRead;
    /**
     * 审阅者
     */
    @ApiModelProperty(value = "审阅者")
    private String gmtReadBy;
}
