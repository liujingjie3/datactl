package com.zjlab.dataservice.modules.storage.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class FileListRequestDto extends PageRequest {

    private String path;

    private String type;

}
