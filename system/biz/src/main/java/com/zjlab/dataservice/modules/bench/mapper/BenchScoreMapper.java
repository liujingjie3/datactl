package com.zjlab.dataservice.modules.bench.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.bench.model.dto.BenchScoreListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * BenchScore 数据库映射接口
 */
@Mapper
public interface BenchScoreMapper extends BaseMapper<BenchScore>  {

    // 根据 model_id 和 task_identifier 查询数据
    List<BenchScore> selectByModelAndTask(IPage<BenchScore> page, @Param("request") BenchScoreListDto request);

}

