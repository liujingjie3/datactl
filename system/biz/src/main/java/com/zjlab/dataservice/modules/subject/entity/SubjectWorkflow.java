package com.zjlab.dataservice.modules.subject.entity;

import java.io.Serializable;
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
 * @Description: 工作流表
 * @Author: jeecg-boot
 * @Date:   2023-12-05
 * @Version: V1.0
 */
@Data
@TableName("subject_workflow")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="subject_workflow对象", description="工作流表")
public class SubjectWorkflow implements Serializable {
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
	/**工作流名称*/
	@Excel(name = "工作流名称", width = 15)
    @ApiModelProperty(value = "工作流名称")
    private String flowName;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
	/**项目Id*/
	@Excel(name = "项目Id", width = 15)
    @ApiModelProperty(value = "项目Id")
    private String projectId;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String flowStatus;
	/**运行时间*/
	@Excel(name = "运行时间", width = 15)
    @ApiModelProperty(value = "运行时间")
    private String runTime;
	/**工作流参数*/
	@Excel(name = "工作流参数", width = 15)
    @ApiModelProperty(value = "工作流参数")
    private String flowData;
}
