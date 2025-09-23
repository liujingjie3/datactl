package com.zjlab.dataservice.modules.notify.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 渠道配置
 */
@Data
@TableName("tc_notify_channel_config")
@ApiModel(value = "NotifyChannelConfig", description = "通知渠道配置")
public class NotifyChannelConfig extends NotifyBasePo {


    private static final long serialVersionUID = -2852282620881529837L;
    @ApiModelProperty("渠道")
    private Byte channel;

    @ApiModelProperty("配置JSON")
    private String configJson;

    @ApiModelProperty("是否启用")
    private Byte enabled;
}

