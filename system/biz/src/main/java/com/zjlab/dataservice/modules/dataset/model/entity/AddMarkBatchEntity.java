package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class AddMarkBatchEntity {

    private List<Integer> ids;

    private Integer markType;

    private Integer status;

    private String updateBy;

    private LocalDateTime updateTime;
}
