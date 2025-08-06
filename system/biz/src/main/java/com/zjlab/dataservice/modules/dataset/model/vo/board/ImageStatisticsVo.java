package com.zjlab.dataservice.modules.dataset.model.vo.board;

import lombok.Data;

import java.util.List;

@Data
public class ImageStatisticsVo {

    //total，数据库总条数
    private Integer total;

    //按月统计数量列表
    private List<PeriodStatisticsVo> monthlyStatistics;
}
