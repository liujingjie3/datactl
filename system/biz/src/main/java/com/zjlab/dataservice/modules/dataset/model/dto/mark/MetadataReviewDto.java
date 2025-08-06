package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MetadataReviewDto {

    @NotNull(message = "文件id不可为空")
    private Integer id;

    @NotNull(message = "审查是否通过不可为空")
    private Integer review;
}
