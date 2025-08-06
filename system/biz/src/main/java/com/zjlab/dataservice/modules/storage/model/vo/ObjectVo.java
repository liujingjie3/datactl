package com.zjlab.dataservice.modules.storage.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class ObjectVo {

    private String name;

    private Date lastModifiedTime;

    private Long size;

    private Boolean isDir;

    private String md5;
}
