package com.zjlab.dataservice.modules.taskplan.model.po;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import com.zjlab.dataservice.modules.taskplan.model.vo.TargetInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 预规划数据管理表
 *
 * @TableName task_manage
 */
@Data
@TableName(value = "task_manage")
@EqualsAndHashCode(callSuper = false)

@Accessors(chain = true)
public class TaskManagePo extends BasePo implements Serializable {

    private String thumbFile;


    private String taskName;


    private String taskInfo;

    @ApiModelProperty("用户id")
    private String userId;
    /**
     * 成像区域的GEOJSON
     */
    @ApiModelProperty("用户框选的成像区域")
    private String geom;

    @ApiModelProperty("成像区域的GEOJSON")
    private String preplanGeom;


    private String imageArea;

    /**
     * 任务类型
     */
    @ApiModelProperty("任务类型")
    private String taskType;

    /**
     * 是否需要拍照
     */
    @ApiModelProperty("是否需要拍照")
    private Boolean needPhoto;


    /**
     * 成像起始时间
     */
    @ApiModelProperty("成像起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /**
     * 成像终止时间
     */
    @ApiModelProperty("成像终止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /**
     * 卫星列表
     */
    @ApiModelProperty("卫星列表")
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
    private Integer predictNumber;
    /**
     * 实际影像数量
     */
    @ApiModelProperty("实际影像数量")
    private Integer number;

    private String level;


    private String messageType;

    private String originator;

    private String recipient;
    private String planType;

    private int planStatus;

    private String targetInfoList;

    private long creationTime;

}
