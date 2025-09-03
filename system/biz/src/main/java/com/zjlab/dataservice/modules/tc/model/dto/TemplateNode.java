package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Template node definition parsed from template_attr
 */
@Data
public class TemplateNode implements Serializable {
    private static final long serialVersionUID = 4803433644452599632L;
    private String key;
    private Long nodeId;
    private String nodeName;
    private String nodeDescription;
    private Integer orderNo;
    private List<String> prev;
    private List<String> next;
    private List<String> roleIds;
    private Integer maxDuration;
}

