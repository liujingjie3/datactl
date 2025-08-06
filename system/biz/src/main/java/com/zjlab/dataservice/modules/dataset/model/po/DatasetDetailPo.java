package com.zjlab.dataservice.modules.dataset.model.po;

import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DatasetDetailPo extends BasePo {

    //文件名称
    private String name;

    //文件存储路径
    private String fileUrl;

    //文件原始存储路径
    private String oriUrl;

    //缩略图oss地址
    private String thumbUrl;

    //用途：1-验证; 2-训练
    private Integer purpose;

    //数据集大小，单位KB
    private Long dataSize;

    //样本尺寸
    private String sampleSize;

    //分辨率
    private String resolution;

    //文件说明
    private String description;
}
