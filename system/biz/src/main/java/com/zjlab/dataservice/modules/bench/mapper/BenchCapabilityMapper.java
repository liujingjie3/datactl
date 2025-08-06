package com.zjlab.dataservice.modules.bench.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.bench.model.entity.BenchCapability;
import org.apache.ibatis.annotations.Mapper;


/**
 * BenchCapability 数据库映射接口
 */
@Mapper
public interface BenchCapabilityMapper extends BaseMapper<BenchCapability>  {

    // 自定义函数

}

