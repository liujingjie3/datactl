package com.zjlab.dataservice.modules.screen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.screen.mapper.RegionalStatisticsMapper;
import com.zjlab.dataservice.modules.screen.model.po.RegionalStatisticsPo;
import com.zjlab.dataservice.modules.screen.service.RegionalStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class RegionalStatisticsServiceImpl extends ServiceImpl<RegionalStatisticsMapper, RegionalStatisticsPo> implements RegionalStatisticsService {
    @Override
    public Double getCoverageRate(String regionCode, String year, String imageType) {
        QueryWrapper<RegionalStatisticsPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RegionalStatisticsPo::getRegionCode, regionCode)
                .eq(RegionalStatisticsPo::getYear, year)
                .eq(RegionalStatisticsPo::getImageType, imageType)
                .eq(RegionalStatisticsPo::getDeleted,false);
        RegionalStatisticsPo regionalStatisticsPo = baseMapper.selectOne(wrapper);
        if (regionalStatisticsPo == null || regionalStatisticsPo.getCoverageRate() == null){
            return 0.0;
        }else {
            return regionalStatisticsPo.getCoverageRate();
        }
    }

    @Override
    public void initRegionalStatistics() {
        baseMapper.truncateTable();
        baseMapper.insertProvinceGF2();
        baseMapper.insertProvinceGF3();
        baseMapper.insertCityGF2();
        baseMapper.insertCityGF3();
        baseMapper.insertTotalProvinceGF2();
        baseMapper.insertTotalProvinceGF3();
        baseMapper.insertTotalCityGF2();
        baseMapper.insertTotalCityGF3();
    }

    @Override
    public void updateProvinceStatistics() {
        //覆盖率计算
        baseMapper.updateCoverageAreaProvinceGF2();
        baseMapper.updateCoverageAreaProvinceGF3();
        baseMapper.updateTotalCoverageAreaProvinceGF2();
        baseMapper.updateTotalCoverageAreaProvinceGF3();
        baseMapper.calculateCoverageRate();
    }

    @Override
    public void updateCityStatistics() {
        //覆盖率计算
        baseMapper.updateCoverageAreaCityGF2();
        baseMapper.updateCoverageAreaCityGF3();
        baseMapper.updateTotalCoverageAreaCityGF2();
        baseMapper.updateTotalCoverageAreaCityGF3();
        baseMapper.calculateCoverageRate();
    }
}
