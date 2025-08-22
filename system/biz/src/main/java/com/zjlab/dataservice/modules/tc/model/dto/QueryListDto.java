package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data

public class QueryListDto extends PageRequest {
    @ApiModelProperty(value = "搜索关键词")
    private String keyword;

}
