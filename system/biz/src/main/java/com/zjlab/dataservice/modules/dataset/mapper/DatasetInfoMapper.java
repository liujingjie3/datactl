package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.entity.DatasetInfoEntity;
import com.zjlab.dataservice.modules.dataset.model.entity.DatasetStatisticsEntity;
import com.zjlab.dataservice.modules.dataset.model.entity.MonthlyPeriodEntity;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetInfoPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatasetInfoMapper extends BaseMapper<DatasetInfoPo> {

    IPage<DatasetInfoPo> qryDatasetList(IPage<DatasetInfoPo> page, @Param("request") DatasetInfoEntity request);

    void addViewCount(Integer id);

    void addSubscribe(Integer datasetInfoId);

    void unSubscribe(Integer datasetInfoId);

    void addCollect(Integer datasetInfoId);

    void unCollect(Integer datasetInfoId);

    void addDownload(Integer datasetInfoId);

    DatasetStatisticsEntity qryDatasetStatistics();

    List<MonthlyPeriodEntity> qryMonthlyList();
}
