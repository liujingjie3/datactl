package com.zjlab.dataservice.modules.storage.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ObjectUploadDto {

    @NotNull
    private String bucketName;

    @NotNull
    private String path;

    private String type = "minio";
}
