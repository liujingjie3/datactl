package com.zjlab.dataservice.modules.taskplan.model.po;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* 
* @TableName task_manage_record
*/
@Data
@TableName(value = "task_manage_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TaskManageRecordPo implements Serializable {

    /**
    * 
    */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
    * 任务id
    */
    @ApiModelProperty("任务id")
    private Integer taskId;
    /**
    * 修改状态
    */
    @ApiModelProperty("修改状态")
    private Integer status;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;

}
