package com.zjlab.dataservice.modules.taskplan.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 预规划数据管理表
 *
 * @TableName task_manage
 */
@Data
@TableName(value = "task_manage")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class TaskManageVo {

    /**
     * id，自增
     */
    @NotNull(message = "[id，自增]不能为空")
    @ApiModelProperty("id，自增")
    private Integer id;

    @Size(max = 45, message = "编码长度不能超过255")
    @ApiModelProperty("任务名称")
    @Length(max = 45, message = "编码长度不能超过255")
    private String thumbFile;

    /**
     * 任务名称
     */
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("任务名称")
    @Length(max = 255, message = "编码长度不能超过255")
    private String taskName;
    /**
     * 任务申请用途
     */
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("任务申请用途")
    @Length(max = 255, message = "编码长度不能超过255")
    private String taskInfo;
    /**
     * 用户id
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("用户id")
    @Length(max = 32, message = "编码长度不能超过32")
    @JsonIgnore
    private String userId;
    /**
     * 成像区域的GEOJSON
     */
    @Size(max = 1000, message = "编码长度不能超过32")
    @ApiModelProperty("成像区域的GEOJSON")
    @Length(max = 1000, message = "编码长度不能超过32")
    private String geom;

    @Size(max = 10000, message = "编码长度不能超过32")
    @ApiModelProperty("成像区域的GEOJSON")
    @Length(max = 10000, message = "编码长度不能超过32")
    private String preplanGeom;

    private String imageArea;
    /**
     * 成像起始时间
     */
    @ApiModelProperty("成像起始时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /**
     * 成像终止时间
     */
    @ApiModelProperty("成像终止时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /**
     * 卫星列表
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("卫星列表")
    @Length(max = 32, message = "编码长度不能超过32")
    private String satelliteInfo;
    /**
     * 任务状态：1.进行中 2.失败 3.已完成
     */
    @ApiModelProperty("任务状态：1.进行中 2.失败 3.已完成")
    private Integer status;
    /**
     * 预计影像数量
     */
    @ApiModelProperty("预计影像数量")
    @JsonIgnore
    private Integer predictNumber;
    /**
     * 实际影像数量
     */
    @ApiModelProperty("实际影像数量")
    @JsonIgnore
    private Integer number;
    /**
     * 影像级别
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("影像级别")
    @Length(max = 32, message = "编码长度不能超过32")
    private String level;

}
