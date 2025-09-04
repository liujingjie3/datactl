package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.modules.taskplan.model.entity.CommandEntity;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TcTaskAddDto {
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务申请用途
     */
    private String taskInfo;


    private List<CommandEntity> comandList;

    /**
     * 卫星列表
     */

    private List<String> satelliteInfo;

    private Integer from;

    /**
     * 影像级别
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("影像级别")
    @Length(max = 32, message = "编码长度不能超过32")
    private String level;
}
