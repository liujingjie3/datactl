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
@TableName("tc_todo_urge_task")
@ApiModel(value = "TodoUrgeTask对象", description = "实例任务催办信息表")
public class TodoUrgeTask extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String instanceId;
    /**
     * 实例编码
     */
    @ApiModelProperty(value = "实例编码")
    private String instanceCode;
    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**
     * 任务编码
     */
    private String taskCode;

    /**
     *
     */
    private String reason;


}

