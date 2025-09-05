package com.zjlab.dataservice.modules.notify.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通知模板配置
 */
@Data
@TableName("tc_notify_template")
@ApiModel(value = "NotifyTemplate", description = "通知模板配置")
public class NotifyTemplate extends BasePo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("业务类型")
    private Byte bizType;

    @ApiModelProperty("渠道")
    private Byte channel;

    @ApiModelProperty("标题模板")
    private String titleTpl;

    @ApiModelProperty("正文模板")
    private String contentTpl;

    @ApiModelProperty("是否启用")
    private Byte enabled;
}

