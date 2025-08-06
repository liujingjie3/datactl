package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class MetadataFilterEntity {

    private List<Integer> ids;

    private Integer status;

    private Boolean filterPassed;

    private String updateBy;

    private LocalDateTime updateTime;

}
