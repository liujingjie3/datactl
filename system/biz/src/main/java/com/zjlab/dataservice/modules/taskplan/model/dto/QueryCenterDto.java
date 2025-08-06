package com.zjlab.dataservice.modules.taskplan.model.dto;

import lombok.Data;

import java.util.List;
@Data
public class QueryCenterDto {
    private List<SubQueryCenterParam> conditions;
}
