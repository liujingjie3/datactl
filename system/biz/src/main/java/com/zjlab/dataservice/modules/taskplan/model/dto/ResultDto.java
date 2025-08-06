package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.modules.taskplan.model.vo.TargetInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class ResultDto {
    private Integer planStatus;

    private Integer planId;
    private String messageType;
    private String originator;
    private String recipient;
    private long creationTime;
    private String planType;
    private List<TargetInfoVo> targetInfoList;

}
