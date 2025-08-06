package com.zjlab.dataservice.modules.bench.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

/**
 * BenchModel 查询列表 DTO
 */
@Data
public class BenchDatasetListDto extends PageRequest {
    private String datasetName;
    private String datasetType;
    private Integer pageNo;
    private Integer pageSize;
}
