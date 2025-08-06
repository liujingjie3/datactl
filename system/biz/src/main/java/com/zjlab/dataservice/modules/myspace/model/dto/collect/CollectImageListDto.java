package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class CollectImageListDto extends PageRequest {

    private String path = "/";
}
