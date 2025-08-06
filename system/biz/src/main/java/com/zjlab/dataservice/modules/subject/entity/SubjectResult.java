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
 * @Description: 专题计算结果表
 * @Author: jeecg-boot
 * @Date:   2023-12-05
 * @Version: V1.0
 */
@Data
@TableName("subject_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="subject_result对象", description="专题计算结果表")
public class SubjectResult implements Serializable {
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
	/**作业名称*/
	@Excel(name = "作业名称", width = 15)
    @ApiModelProperty(value = "作业名称")
    private String flowName;
	/**计算方式*/
	@Excel(name = "计算方式", width = 15)
    @ApiModelProperty(value = "计算方式")
    private String calculationMethod;
	/**所属工作流*/
	@Excel(name = "所属工作流", width = 15)
    @ApiModelProperty(value = "所属工作流")
    private String flowId;
	/**所属任务*/
	@Excel(name = "所属任务", width = 15)
    @ApiModelProperty(value = "所属任务")
    private String taskId;
	/**采纳情况*/
	@Excel(name = "采纳情况", width = 15)
    @ApiModelProperty(value = "采纳情况")
    private String status;
	/**项目ID*/
	@Excel(name = "项目ID", width = 15)
    @ApiModelProperty(value = "项目ID")
    private String projectId;
}
