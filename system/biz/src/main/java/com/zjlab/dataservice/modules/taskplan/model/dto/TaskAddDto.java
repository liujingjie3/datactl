package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskAddDto {
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务申请用途
     */
    private String taskInfo;

    /**
     * 成像区域的GEOJSON
     */
    private String geom;


        private List<PreplanDataPo> preplanGeom;
//    private List<PreplanDataEntity> preplanGeom;

    private String imageArea;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 是否需要拍照
     */
    private Boolean needPhoto;

    /**
     * 成像起始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 卫星列表
     */

    private List<String> satelliteInfo;
    /**
     * 预计影像数量
     */
    @ApiModelProperty("预计影像数量")
    private Integer predictNumber;

    /**
     * 影像级别
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("影像级别")
    @Length(max = 32, message = "编码长度不能超过32")
    private String level;
}
