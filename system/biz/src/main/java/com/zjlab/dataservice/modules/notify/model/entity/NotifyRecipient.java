package com.zjlab.dataservice.modules.notify.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通知收件人明细
 */
@Data
@TableName("tc_notify_recipient")
@ApiModel(value = "NotifyRecipient", description = "通知收件人明细")
public class NotifyRecipient extends NotifyBasePo {


    private static final long serialVersionUID = -5788599433990527415L;
    @ApiModelProperty("任务ID")
    private Long jobId;

    @ApiModelProperty("接收人用户ID")
    private String userId;

    @ApiModelProperty("状态0待发1成功2失败3取消4异常结束")
    private Byte status;

    @ApiModelProperty("失败原因")
    private String lastError;

    @ApiModelProperty("第三方消息ID")
    private String externalMsgId;
}

