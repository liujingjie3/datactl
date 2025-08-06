package com.zjlab.dataservice.modules.screen.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.screen.model.po.RegionalStatisticsPo;

@DS("postgre")
public interface RegionalStatisticsMapper extends BaseMapper<RegionalStatisticsPo> {

    //*********************  init  *************************//
    void truncateTable();
    //总量统计
    void insertTotalProvinceGF2();
    void insertTotalProvinceGF3();
    void insertTotalCityGF2();
    void insertTotalCityGF3();
    //按年份统计
    void insertProvinceGF2();
    void insertProvinceGF3();
    void insertCityGF2();
    void insertCityGF3();
    //********************************************************//

    //*********************  period  *************************//
    //====================  province  ========================//
    //coverage
    void updateCoverageAreaProvinceGF2();
    void updateCoverageAreaProvinceGF3();
    void updateTotalCoverageAreaProvinceGF2();
    void updateTotalCoverageAreaProvinceGF3();
    //====================  city  ========================//
    //coverage
    void updateCoverageAreaCityGF2();
    void updateCoverageAreaCityGF3();
    void updateTotalCoverageAreaCityGF2();
    void updateTotalCoverageAreaCityGF3();
    //====================  district  ========================//

    //================  calculate rate  ======================//
    void calculateCoverageRate();
}
