package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.modules.tc.model.entity.SatellitePasses;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS("postgre")
public interface SatellitePassesMapper extends BaseMapper<SatellitePasses> {
    List<SatellitePasses> queryPassInfo(@Param("satIds") List<String> satIds);


}




