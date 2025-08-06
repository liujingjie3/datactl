package com.zjlab.dataservice.modules.bench.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.bench.model.dto.BenchDatasetListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BenchDatasetMapper extends BaseMapper<BenchDataset>  {

//    IPage<BenchDataset> qryBenchDatasetList(IPage<BenchDataset> page, @Param("request") BenchDatasetListDto request);

}
