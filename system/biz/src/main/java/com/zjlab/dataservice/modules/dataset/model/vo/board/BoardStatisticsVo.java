package com.zjlab.dataservice.modules.dataset.model.vo.board;

import lombok.Data;

@Data
public class BoardStatisticsVo {

    private MarkStatisticsVo markStatisticsVo;

    private ImageStatisticsVo imageStatisticsVo;

    private DatasetStatisticsVo datasetStatisticsVo;
}
