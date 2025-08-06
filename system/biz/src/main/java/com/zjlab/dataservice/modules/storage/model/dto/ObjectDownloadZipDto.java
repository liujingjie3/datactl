package com.zjlab.dataservice.modules.storage.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ObjectDownloadZipDto {

    @NotNull
    private String bucketName;

    @NotNull
    private List<String> pathList;

    private String fileName;

    private String type = "minio";
}
