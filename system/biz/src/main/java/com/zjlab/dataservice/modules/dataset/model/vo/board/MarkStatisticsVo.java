package com.zjlab.dataservice.modules.dataset.model.vo.board;

import lombok.Data;

@Data
public class MarkStatisticsVo {

    //数据总量 total
    private Integer total;

    //待标注总量 status=2
    private Integer unMarkNum;

    //已分配量 status=4
    private Integer assignNum;

    //筛选比率(status!=0&&status!=1)/total
    private Double filterRatio;

    //已初标量 status=3
    private Integer markedNum;

    //已复查 status=5
    private Integer checkedNum;

    //数据核查率 5/(4+5)
    private Double checkRatio;
}
