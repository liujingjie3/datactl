package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tc_todo_event_fail")
@ApiModel(value = "TodoEventFail对象", description = "任务中心发送失败事件信息表")
public class TodoEventFail extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CorpID
     */
    @ApiModelProperty(value = "CorpID")
    private String toUserName;
    /**
     * 成员userID
     */
    @ApiModelProperty(value = "成员userID")
    private String fromUserName;
    /**
     * 事件时间戳
     */
//    @ApiModelProperty(value = "事件时间戳")
//    private Long createTime;
    /**
     * 事件唯一标识
     */
    @ApiModelProperty(value = "事件唯一标识")
    private String eventId;
    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;
    /**
     * 事件内容
     */
    @ApiModelProperty(value = "事件内容")
    private String event;
    /**
     * 应用id
     */
    @ApiModelProperty(value = "应用id")
    private String agentId;
    /**
     * 发送失败异常信息
     */
    @ApiModelProperty(value = "发送失败异常信息")
    private String message;
    /**
     * 调用的接口
     */
    @ApiModelProperty(value = "调用的接口")
    private String requestUri;


}
