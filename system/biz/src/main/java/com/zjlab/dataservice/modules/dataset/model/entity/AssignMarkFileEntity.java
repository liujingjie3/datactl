package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class AssignMarkFileEntity {

    private List<Integer> ids;

    private String marker;

    private String checker;

    private Integer status;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String classifyMarker;

    private String classifyChecker;

    private Integer classifyStatus;

    private String classifyUpdateBy;

    private LocalDateTime classifyUpdateTime;

    private String segmentMarker;

    private String segmentChecker;

    private Integer segmentStatus;

    private String segmentUpdateBy;

    private LocalDateTime segmentUpdateTime;
}
