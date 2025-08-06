package com.zjlab.dataservice.modules.dataset.model.vo.board;

import lombok.Data;

import java.util.List;

@Data
public class DatasetStatisticsVo {

    //total，数据库总条数
    private Integer total;

    //入库数，已公开，isPublic=true
    private Integer publicNum;

    //入库数 / 影像数据集总量
    private Double publicRatio;

    //按月统计数量列表
    private List<PeriodStatisticsVo> periodStatistics;
}
