package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.entity.*;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetMarkFilePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MarkFileMapper extends BaseMapper<DatasetMarkFilePo> {

    IPage<DatasetMarkFilePo> qryMarkFileList(IPage<DatasetMarkFilePo> page, @Param("request") MarkListEntity request);

    int assignMarkFile(@Param("entity") AssignMarkFileEntity entity);

    int selectExistIds(@Param("ids") List<Integer> ids, @Param("status") Integer status, @Param("taskType") String taskType);

    int filter(@Param("entity") MarkFilterEntity entity);

    int addMarkBatch(@Param("entity") AddMarkBatchEntity entity);

    List<StatusEntity> qryStatusCount();

    List<MarkStatisticsRankEntity> qryStatisticsRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<WeeklyPeriodEntity> qryWeeklyList();

    List<DailyEntity> qrySegmentDailyStatistics();

    List<DailyEntity> qryClassifyDailyStatistics();

    List<DailyEntity> qryTextDailyStatistics();

    MarkReviewEntity qryMarkReviewStatistics();

}
