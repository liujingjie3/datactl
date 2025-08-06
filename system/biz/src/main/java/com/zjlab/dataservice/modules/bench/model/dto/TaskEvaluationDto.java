package com.zjlab.dataservice.modules.bench.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

/**
 * TaskEvaluation 查询列表 DTO
 */
@Data
public class TaskEvaluationDto extends PageRequest {
    private Integer modelId;
    private Integer capabilityId;
    private Integer pageNo;
    private Integer pageSize;
}
