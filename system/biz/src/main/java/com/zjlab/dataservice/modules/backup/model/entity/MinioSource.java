package com.zjlab.dataservice.modules.backup.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MinioSource {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String path;
}
