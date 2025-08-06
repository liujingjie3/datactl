package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class MarkListEntity {

    private String userId;

    private String query;

    private Integer markType;

    private String marker;

    private String checker;

    private Integer status;

    private Integer purpose;

    private List<Integer> reviews;

    private String segmentUrl;

    private Integer pageNo;

    private Integer pageSize;

    private String orderByField;

    private String orderByType;

    private String taskType;
}
