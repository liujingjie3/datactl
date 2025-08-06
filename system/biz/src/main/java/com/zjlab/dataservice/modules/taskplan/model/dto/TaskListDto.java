package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class TaskListDto extends PageRequest {

    private String userId;
}
