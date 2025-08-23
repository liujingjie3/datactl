package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Template node definition parsed from template_attr
 */
@Data
public class TemplateNode {
    private String key;
    private Long nodeId;
    private Integer orderNo;
    private List<String> prev;
    private List<String> next;
    private List<String> roleIds;
    private Integer maxDuration;
}

