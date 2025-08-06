package com.zjlab.dataservice.modules.backup.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskStartDto {

    //文件id
    @NotNull(message = "任务id不可为空")
    private Integer id;
}
