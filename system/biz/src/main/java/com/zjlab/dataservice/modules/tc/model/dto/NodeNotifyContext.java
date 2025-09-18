package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知使用的节点上下文
 */
@Data
public class NodeNotifyContext implements Serializable {

    private static final long serialVersionUID = -5012584694309676380L;

    private String taskName;
    private String nodeName;
    private Integer maxDuration;
    private LocalDateTime startedAt;
}
