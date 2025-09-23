package com.zjlab.dataservice.modules.notify.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.tc.model.dto.TcBaseEntityDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通知模板配置
 */
@Data
@TableName("tc_notify_template")
@ApiModel(value = "NotifyTemplate", description = "通知模板配置")
public class NotifyTemplate extends TcBaseEntityDto {


    private static final long serialVersionUID = 2281835906780779999L;
    @ApiModelProperty("业务类型")
    private Byte bizType;

    @ApiModelProperty("渠道")
    private Byte channel;

    @ApiModelProperty("标题模板")
    private String titleTpl;

    @ApiModelProperty("正文模板")
    private String contentTpl;

    @ApiModelProperty("三方平台模板ID")
    private String externalTplId;

    @ApiModelProperty("是否启用")
    private Byte enabled;
}

