package com.zjlab.dataservice.modules.dataset.model.dto.thematic;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class ThematicDataDelDto {

    @NotNull
    private Integer id;
}
