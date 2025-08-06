package com.zjlab.dataservice.modules.bench.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

/**
 * BenchTask 查询列表 DTO
 */
@Data
public class BenchTaskListDto extends PageRequest {
    private String taskNameEn;
    private String taskIdentifier;
    private Integer capabilityId;
    private Integer pageNo;
    private Integer pageSize;
}
