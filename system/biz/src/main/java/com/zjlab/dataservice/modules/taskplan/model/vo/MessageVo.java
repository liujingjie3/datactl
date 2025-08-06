package com.zjlab.dataservice.modules.taskplan.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
@Data
public class MessageVo {
    private Integer planId;
    private String messageType;
    private String originator;
    private String recipient;
    private long creationTime;
    private String planType;
    private List<TargetInfoVo> targetInfoList;

}
