package com.zjlab.dataservice.modules.storage.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileVo {

    private String name;

    private String path;

    private Long lastModifiedTime;

    private Long size;

    private Boolean isDir;

    private String md5;

    private String permissions;
}
