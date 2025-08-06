package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MarkAssignDto {

    @NotNull(message = "标注人不能为空")
    private String marker;

    @NotNull(message = "带标注文件入参不能为null")
    @NotEmpty(message = "标注文件id列表不能为空数组")
    private List<@NotNull(message = "标注文件id列表不能含有null值") Integer> ids;

    @NotNull(message = "待分派文件状态不可为空")
    private Integer status;

    @NotNull(message = "任务类型不可为空")
    private String taskType;
}
