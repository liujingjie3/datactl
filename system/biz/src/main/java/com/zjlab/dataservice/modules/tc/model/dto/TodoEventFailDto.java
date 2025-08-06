package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "TodoEventVO对象", description = "任务中心事件信息表")
public class TodoEventFailDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = -8975979298952045949L;

    private Long id;
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
    @ApiModelProperty(value = "事件时间戳")
    private Long createTime;
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

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime creatTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
