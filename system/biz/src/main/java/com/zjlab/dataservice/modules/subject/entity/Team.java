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
 * @Description: 团队表
 * @Author: jeecg-boot
 * @Date: 2023-09-13
 * @Version: V1.0
 */
@Data
@TableName("subject_team")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "subject_team对象", description = "团队表")
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 团队名称
     */
    @Excel(name = "团队名称", width = 15)
    @ApiModelProperty(value = "团队名称")
    private String teamName;

    /**
     * 团队描述
     */
    @Excel(name = "团队描述", width = 15)
    @ApiModelProperty(value = "团队描述")
    private String teamDescribe;

    /**
     * 团队编码
     */
    @Excel(name = "团队编码", width = 15)
    @ApiModelProperty(value = "团队编码")
    private String teamCode;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称", width = 15)
    @ApiModelProperty(value = "部门名称")
    private String departNames;
    ;


}
