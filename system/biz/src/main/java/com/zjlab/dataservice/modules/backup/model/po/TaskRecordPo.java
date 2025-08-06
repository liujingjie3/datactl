package com.zjlab.dataservice.modules.backup.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("backup_task_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TaskRecordPo extends BasePo {

    //任务id
    private Integer taskId;
    //源数据源id
    private Integer sourceId;
    //源数据源类型
    private String sourceType;
    //目的数据源id
    private Integer destId;
    //目的数据源类型
    private String destType;
    //定时任务时间信息
    private String cron;
    //执行状态：待执行，执行中，停止中，已结束
    private String status;
    //子状态：成功，失败，人工终止
    private String subStatus;
    //运行结果日志
    private String logInfo;
}
