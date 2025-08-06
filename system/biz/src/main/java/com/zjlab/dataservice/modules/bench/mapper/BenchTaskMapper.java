package com.zjlab.dataservice.modules.bench.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchCapability;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * BenchTask 数据库映射接口
 */
@Mapper
public interface BenchTaskMapper extends BaseMapper<BenchTask>  {

    // 自定义函数
    IPage<BenchTask> qryBenchTaskList(IPage<BenchTask> page, @Param("request") BenchTaskListDto request);

}

