package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class DatasetInfoEntity {

    //搜索框输入条件
    private String query;

    //任务类型, 传入字典id
    private Integer taskType;

    //数据标签,多个字段,英文标签分割
    private List<String> tags;

    //是否启用
    private Boolean isPublic;

    private String orderByField;

    private String orderByType;
}
