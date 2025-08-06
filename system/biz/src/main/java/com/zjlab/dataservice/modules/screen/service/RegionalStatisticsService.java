package com.zjlab.dataservice.modules.screen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.screen.model.po.RegionalStatisticsPo;

public interface RegionalStatisticsService extends IService<RegionalStatisticsPo> {

    Double getCoverageRate(String regionCode, String year, String imageType);

    void initRegionalStatistics();

    void updateProvinceStatistics();

    void updateCityStatistics();
}
