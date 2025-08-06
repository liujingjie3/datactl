package com.zjlab.dataservice.modules.taskplan.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PreplanDataDto extends PageRequest {

    private List<SubQueryParam> conditions;
    //空间搜索
    private String geometry;

    private Integer day;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
