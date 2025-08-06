package com.zjlab.dataservice.modules.bench.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.dto.TaskEvaluationDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.model.entity.TaskEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * BenchTask 数据库映射接口
 */
@Mapper
public interface TaskEvaluationMapper extends BaseMapper<TaskEvaluation>  {

    // 自定义函数
    IPage<TaskEvaluation> selectByModelAndCapability(IPage<TaskEvaluation> page, @Param("request") TaskEvaluationDto request);

}

