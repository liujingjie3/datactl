package com.zjlab.dataservice.modules.screen.service;

import com.zjlab.dataservice.modules.screen.model.vo.*;

import java.util.List;

public interface DataScreenService {

    SliceStatisticsVo qrySliceStatistics();

    List<TimeSequenceVo> qryDataTimeSequence(String regionCode);

    List<YearSequenceVo<DataRegionStatisticsVo>> qryDataRegionStatistics(String regionCode);

}
