package com.zjlab.dataservice.modules.dataset.model.vo.dataset;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class DatasetFileVo {

    private Integer id;

    private Integer datasetId;

    //文件名
    private String name;

    //文件存储路径
    private String fileUrl;

    //文件原始存储路径
    private String oriUrl;

    //缩略图oss地址
    private String thumbUrl;

    //用途：1-验证，2-训练
    private Integer purpose;

    //文件大小
    private Long dataSize;

    //样本尺寸
    private String sampleSize;

    //分辨率
    private String resolution;

    //文件说明
    private String description;

    private String updateBy;

    private LocalDateTime updateTime;


}
