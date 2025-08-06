package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class MetadataListEntity {

    private String userId;

    private String query;

    private String marker;

    private String checker;

    private Boolean filterPassed;

    private Integer pageNo;

    private Integer pageSize;

    private String orderByField;

    private String orderByType;

    private Integer status;

    private List<Integer> reviews;
}
