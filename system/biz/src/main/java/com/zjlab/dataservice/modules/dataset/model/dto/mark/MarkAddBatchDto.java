package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MarkAddBatchDto {

    //文件id
    @NotNull(message = "批量标注文件入参不能为null")
    @NotEmpty(message = "批量标注文件id列表不能为空数组")
    private List<@NotNull(message = "标注文件id列表不能含有null值") Integer> ids;

    @NotNull(message = "打标类型不可为空")
    private Integer markType;
}
