package com.zjlab.dataservice.modules.subject.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 专题任务表
 * @Author: jeecg-boot
 * @Date:   2023-11-27
 * @Version: V1.0
 */
@Data
@TableName("subject_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="subject_task对象", description="专题任务表")
public class SubjectTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**任务标题*/
	@Excel(name = "任务标题", width = 15)
    @ApiModelProperty(value = "任务标题")
    private String taskTitle;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private String projectName;
	/**任务内容*/
	@Excel(name = "任务内容", width = 15)
    @ApiModelProperty(value = "任务内容")
    private String taskContent;
	/**用户名称*/
	@Excel(name = "用户名称", width = 15)
    @ApiModelProperty(value = "用户名称")
    private String uesrName;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;


    @ApiModelProperty(notes = "状态-status（string类型，0未启动、1运行中、2已暂停、3已取消、4已完成)", required = false)
    private String taskStatus;

    @ApiModelProperty(notes = "优先级-priority（string类型，1较低、2普通、3紧急、4非常紧急）", required = false)
    private String taskPriority;

    @ApiModelProperty(notes = "标签-taskLabel（string类型，数字字典多选，多个用逗号隔开）", required = false)
    private String taskLabel;

    @ApiModelProperty(notes = "进度-progress（string类型，0-0%， 1-10%，2-20%，……10-100%）", required = false)
    private String taskProgress;

    @ApiModelProperty(notes = "附件资源-attachment（string类型，长文本，如有多个用逗号隔开）", required = false)
    private String taskAttachment;

    @ApiModelProperty(notes = "启动时间-openTime", required = false)
    private LocalDateTime taskOpenTime;

    @ApiModelProperty(notes = "完成时间-finishTime", required = false)
    private LocalDateTime taskFinishTime;


}
