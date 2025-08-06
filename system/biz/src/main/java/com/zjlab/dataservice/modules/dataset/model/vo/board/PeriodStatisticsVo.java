package com.zjlab.dataservice.modules.dataset.model.vo.board;

import lombok.Data;

@Data
public class PeriodStatisticsVo {

    private Integer year;

    // monthly; weekly; daily
    private String mode;

    //一年中的第几个月/周
    private Integer id;

    private Integer num;

    //yyyy-MM-dd 格式
    private String firstDay;
}
