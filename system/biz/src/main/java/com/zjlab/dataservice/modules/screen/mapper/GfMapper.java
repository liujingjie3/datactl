package com.zjlab.dataservice.modules.screen.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.modules.screen.model.entity.GF2TimeSeqEntity;
import com.zjlab.dataservice.modules.screen.model.entity.RegionDataEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("postgre")
public interface GfMapper {

    //支持区域自选，null值查询全国
    long count(@Param("regionCode") String regionCode);
    //全国
    long selectL2();
    //全国
    long selectL3();

    List<GF2TimeSeqEntity> selectGF2TimeSeq(@Param("regionCode") String regionCode);

    List<RegionDataEntity> selectGF2RegionData(@Param("regionId") String regionId);
}
