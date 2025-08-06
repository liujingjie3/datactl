package com.zjlab.dataservice.modules.bench.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

/**
 * BenchModel 查询列表 DTO
 */
@Data
public class BenchModelListDto  extends PageRequest {
    private String name;
    private String modelType;
    private String sourceType;
    private Integer capabilityId;
    private Integer pageNo;
    private Integer pageSize;
}
