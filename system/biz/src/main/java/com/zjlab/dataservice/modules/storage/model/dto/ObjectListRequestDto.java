package com.zjlab.dataservice.modules.storage.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ObjectListRequestDto extends PageRequest {

    @NotEmpty
    private String bucketName;

    private String prefix = "";

    private Boolean recursive = true;

    private String type = "minio";
}
