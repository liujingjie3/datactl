package com.zjlab.dataservice.modules.storage.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ObjectDownloadDto {

    @NotNull
    private String bucketName;

    @NotNull
    private String path;

    private String fileName;

    private String type = "minio";
}
