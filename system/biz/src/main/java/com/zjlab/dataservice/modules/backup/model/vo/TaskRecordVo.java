package com.zjlab.dataservice.modules.backup.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TaskRecordVo {

    //任务id
    private Integer taskId;
    //任务名称
    private String taskName;
    //数据源id
    private Integer sourceId;
    //数据源名称
    private String sourceName;
    //数据源类型
    private String sourceType;
    //目的源id
    private Integer destId;
    //目的源名称
    private String destName;
    //目的数据源类型
    private String destType;
    //定时任务时间信息
    private String cron;
    //执行状态：待执行，执行中，停止中，已结束
    private String status;
    //子状态：成功，失败，人工终止
    private String subStatus;
    //更新时间
    private LocalDateTime updateTime;
}
