package com.zjlab.dataservice.modules.dataset.model.dto.dataset;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DatasetDetailDto {

    @NotNull
    private Integer id;
}
