package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OperationLogDto {

    @NotNull
    private Integer id;

    @NotNull
    private String taskType;
}
