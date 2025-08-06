package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskWebDto implements Serializable {

    private Long id;

    private String instanceCode;

    private Integer flag;

    private String pcUrl;

    private String h5Url;

    private String title;

    private String fromUserId;

    private Long createOn;
}
