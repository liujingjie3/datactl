package com.zjlab.dataservice.modules.bench.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

/**
 * BenchScore 查询列表 DTO
 */
@Data
public class BenchScoreListDto extends PageRequest {
    private Integer modelId;
    private String taskIdentifier;
    private Integer pageNo;
    private Integer pageSize;
}
