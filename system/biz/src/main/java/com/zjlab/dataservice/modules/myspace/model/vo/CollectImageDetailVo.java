package com.zjlab.dataservice.modules.myspace.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectImageDetailVo {

    //字段待补充
    private Integer id;

    private String fileName;

    private Boolean isDir;

    private LocalDateTime updateTime;

    private String fileSize;

    private Integer applyStatus;

    private String fileUrl;

    private String imageType;

    private String thumbUrl;

    private Float imageGsd;
}
