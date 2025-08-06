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
 * @Description: 专题项目表
 * @Author: jeecg-boot
 * @Date:   2023-11-29
 * @Version: V1.0
 */
@Data
@TableName("suject_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="suject_project对象", description="专题项目表")
public class SujectProject implements Serializable {
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
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private String projectName;
	/**项目标签*/
	@Excel(name = "项目标签", width = 15)
    @ApiModelProperty(value = "项目标签")
    private String projectTag;
	/**项目描述*/
	@Excel(name = "项目描述", width = 15)
    @ApiModelProperty(value = "项目描述")
    private String description;
	/**项目状态*/
	@Excel(name = "项目状态:0未开始；1进行中；2已完成", width = 15)
    @ApiModelProperty(value = "项目状态:0未开始；1进行中；2已完成")
    private String projectStatus;

    /**团队id*/
    @Excel(name = "团队id", width = 15)
    @ApiModelProperty(value = "团队id")
    private String teamId;

    /**项目状态*/
    @Excel(name = "团队名称", width = 15)
    @ApiModelProperty(value = "团队名称")
    private String teamName;

}
