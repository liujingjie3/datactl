package com.zjlab.dataservice.modules.toolbox.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("my_task")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TaskPo {
    /**
     * id
     */
    private Integer id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名
     */
    private String taskName;

    /**
     * 任务状态
     */
    private ToolBoxStatus status;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 任务执行id-暂不用
     */
    private String execId;

    /**
     * 任务执行耗时
     */
    private double costTime;

    /**
     * 任务创建人用户id
     */
    private String userId;

    /**
     * 任务配置
     */
    private String taskConfiguration;

    /**
     * 执行日志
     */
    private String log;

    /**
     * 关联templateId
     */
    private String templateId;

    /**
     * 数据名列表: "xxx,xxx,xxx,xxx"
     */
    private String data;

    /**
     * 任务执行结果
     */
    private String taskResult;

}
