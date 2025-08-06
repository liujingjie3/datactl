package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class MarkSegmentDto {
    //文件id
    @NotNull(message = "文件id不可为空")
    private Integer id;

    @NotNull(message = "分割Json数据不可为空")
    private String segmentJsonData;

    @NotNull(message = "用途不可为空")
    private Integer purpose;

    @NotNull(message = "分割Json地址不可为空")
    private String segmentUrl;
}