package com.zjlab.dataservice.modules.dataset.model.dto.dataset;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class DatasetListDto extends PageRequest {

    //搜索框输入条件
    private String query;

    //任务类型, 传入字典id
    private Integer taskType;

    //数据标签,多个字段,英文标签分割
    private String tags;

    //是否启用
    private Boolean isPublic;
}
