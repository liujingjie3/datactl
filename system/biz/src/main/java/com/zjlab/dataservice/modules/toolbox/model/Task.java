package com.zjlab.dataservice.modules.toolbox.model;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Task {
    /**
     * 自增id
     */
    private int id;

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
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 任务最后编辑时间
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
     * 任务配置, JSON String
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
    @JsonProperty
    private String data;

}

