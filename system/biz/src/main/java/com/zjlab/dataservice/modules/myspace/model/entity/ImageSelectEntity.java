package com.zjlab.dataservice.modules.myspace.model.entity;


import lombok.Data;

import java.util.List;

@Data
public class ImageSelectEntity {

    private List<Integer> ids;

    private Integer pageNo;

    private Integer pageSize;

    private String orderByField;

    private String orderByType;
}
