package com.zjlab.dataservice.modules.toolbox.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.toolbox.enumerate.TaskLogType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("flow_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TaskLogPo {
    /**
     * 自增id
     */
    private int id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * data file id
     */
    private String fileId;

    /**
     * 模块id，步骤:L1->L2,L2->L3,自定义的模块名称
     */
    private String moduleId;

    /**
     * 模块中具体操作
     */
    private String ops;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 运行耗时
     */
    private float runTime;

    /**
     * 进度
     */
    private String stage;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 任务描述
     */
    private int tenantId;

    /**
     * 任务描述
     */
    private int delFlag;

    /**
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 任务最后编辑时间
     */
    private Date updateTime;
}
