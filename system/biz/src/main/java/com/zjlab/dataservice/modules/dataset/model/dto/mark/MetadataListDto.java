package com.zjlab.dataservice.modules.dataset.model.dto.mark;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class MetadataListDto extends PageRequest {

    private String query;

    private String marker;

    private String checker;

    private Integer status;

    private List<Integer> reviews;
}
