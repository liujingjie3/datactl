package com.zjlab.dataservice.modules.screen.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.modules.screen.model.entity.GF3TimeSeqEntity;
import com.zjlab.dataservice.modules.screen.model.entity.RegionDataEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("postgre")
public interface Gf3Mapper {

    //支持区域自选，null值查询全国
    long count(@Param("regionCode") String regionCode);

    List<GF3TimeSeqEntity> selectGF3TimeSeq(@Param("regionCode") String regionCode);

    List<RegionDataEntity> selectGF3RegionData(@Param("regionId") String regionId);

}
