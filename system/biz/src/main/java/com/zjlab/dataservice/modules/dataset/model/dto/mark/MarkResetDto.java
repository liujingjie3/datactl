package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MarkResetDto {

    //文件id
    @NotNull(message = "文件id不可为空")
    private Integer id;
}
