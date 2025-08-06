package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MarkCheckClassDto {

    //文件id
    @NotNull(message = "文件id不可为空")
    private Integer id;

    @NotNull(message = "标注类型不可为空")
    private String markType;

    @NotNull(message = "用途不可为空")
    private Integer purpose;
}
