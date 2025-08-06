package com.zjlab.dataservice.modules.bench.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchModel;
import com.zjlab.dataservice.modules.bench.model.po.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * BenchModel 数据库映射接口
 */
@Mapper
public interface BenchModelMapper extends BaseMapper<BenchModel>  {

    IPage<BenchModelPo> qryBenchModelList(IPage<BenchModelPo> page, @Param("request") BenchModelListDto request);

    IPage<BenchCoarsePo> qryModelCoarseList(IPage<BenchCoarsePo> page, @Param("request") BenchModelListDto request);

    IPage<BenchFinePo> qryModelFineList(IPage<BenchFinePo> page, @Param("request") BenchModelListDto request);

    IPage<BenchLanguagePo> qryModelLanguageList(IPage<BenchLanguagePo> page, @Param("request") BenchModelListDto request);

    IPage<BenchUnderstandingPo> qryModelUnderstandingList(IPage<BenchUnderstandingPo> page, @Param("request") BenchModelListDto request);

    IPage<BenchAnalysisPo> qryModelAnalysisList(IPage<BenchAnalysisPo> page, @Param("request") BenchModelListDto request);

    BenchModel selectModelWithTeamName(@Param("id") Integer id);

}

