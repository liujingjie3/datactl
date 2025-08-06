package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MarkCheckDto {

    //文件id
    @NotNull(message = "文件id不可为空")
    private Integer id;

    //标注文件地址
    @NotBlank(message = "标注文件地址不可为空")
    private String markUrl;

    @NotBlank(message = "xml数据不可为空")
    private String xmlData;

    @NotNull(message = "用途不可为空")
    private Integer purpose;
}
