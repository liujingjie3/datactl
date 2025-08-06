package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskInfoDto extends PageRequest{


    @NotNull(message = "数据ID不能为空")
    private Integer id;
}
