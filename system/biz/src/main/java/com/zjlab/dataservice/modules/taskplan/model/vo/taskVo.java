package com.zjlab.dataservice.modules.taskplan.model.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class taskVo {

    private String messageType; // 消息类型
    private String originator;  // 来源
    private String recipient;   // 目的地
    private String creationTime; // 任务创建时间
    private String planType;    // 任务类型
    private String targetInfoList;
    // 成像信息列表
    private String satelliteId; // 卫星ID
    private String planId;
}