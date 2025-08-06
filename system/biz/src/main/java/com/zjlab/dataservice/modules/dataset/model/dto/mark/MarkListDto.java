package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.modules.dataset.enums.TaskTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class MarkListDto extends PageRequest {

    private String query;

    private String markType;

    private String marker;

    private String checker;

    private Integer status;

    private Integer purpose;

    private List<Integer> reviews;

    private String taskType;
}
