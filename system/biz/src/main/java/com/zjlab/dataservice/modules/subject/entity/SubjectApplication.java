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
 * @Description: 应用表
 * @Author: jeecg-boot
 * @Date:   2023-11-06
 * @Version: V1.0
 */
@Data
@TableName("subject_application")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="subject_application对象", description="应用表")
public class SubjectApplication implements Serializable {
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
	/**应用名称*/
	@Excel(name = "应用名称", width = 15)
    @ApiModelProperty(value = "应用名称")
    private String name;
	/**封面图片*/
	@Excel(name = "封面图片", width = 15)
    @ApiModelProperty(value = "封面图片")
    private String coverUrl;
	/**介绍*/
	@Excel(name = "介绍", width = 15)
    @ApiModelProperty(value = "介绍")
    private String introduce;
	/**分类*/
	@Excel(name = "分类", width = 15)
    @ApiModelProperty(value = "分类")
    private String category;
	/**标签*/
	@Excel(name = "标签", width = 15)
    @ApiModelProperty(value = "标签")
    private String appLabel;
	/**状态*/
	@Excel(name = "状态：1 为上架 0 为下架", width = 15)
    @ApiModelProperty(value = "状态：1 为上架 0 为下架")
    private Boolean status;

    /**状态*/
    @Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
    private String appVersion;

    /**状态*/
    @Excel(name = "应用编码", width = 15)
    @ApiModelProperty(value = "应用编码")
    private String appCode;


    /**状态*/
    @Excel(name = "应用地址", width = 15)
    @ApiModelProperty(value = "应用地址")
    private String appUrl;







}
