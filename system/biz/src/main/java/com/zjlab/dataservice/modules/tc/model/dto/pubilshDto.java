package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data

public class pubilshDto extends PageRequest {
    @ApiModelProperty(value = "搜索关键词")
    private Long id;

    private Integer flag;

}
