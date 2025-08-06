package com.zjlab.dataservice.modules.dataset.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("dataset_operation_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DatasetOperationLogPo extends BasePo {

    //数据集id
    private Integer datasetId;

    //文件id
    private Integer fileId;

    //操作类型
    private String operation;

    //标注文件地址
    private String markUrl;

    //分类类别
    private String markType;

    //上一次分类类别
    private String lastMarkType;

    //任务类型
    private String taskType;
}
