package com.zjlab.dataservice.modules.notify.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.tc.model.dto.TcBaseEntityDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知任务表
 */
@Data
@TableName("tc_notify_job")
@ApiModel(value = "NotifyJob", description = "通知任务")
public class NotifyJob extends TcBaseEntityDto {


    private static final long serialVersionUID = 1181807558798720880L;
    @ApiModelProperty("业务类型")
    private Byte bizType;

    @ApiModelProperty("业务ID")
    private Long bizId;

    @ApiModelProperty("幂等去重键")
    private String dedupKey;

    @ApiModelProperty("业务上下文JSON")
    private String payload;

    @ApiModelProperty("渠道")
    private Byte channel;

    @ApiModelProperty("状态0待处理1成功2失败3取消4异常结束")
    private Byte status;

    @ApiModelProperty("已重试次数")
    private Integer retryCount;

    @ApiModelProperty("下一次运行时间")
    private LocalDateTime nextRunTime;

    @ApiModelProperty("备注")
    private String remark;
}

