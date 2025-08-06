package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MetadataReviewBatchDto {

    //文件id列表
    @NotNull(message = "复审文件入参不能为null")
    @NotEmpty(message = "复审文件id列表不能为空数组")
    private List<@NotNull(message = "复审文件id列表不能含有null值") Integer> ids;

    @NotNull(message = "审查是否通过不可为空")
    private Integer review;
}
