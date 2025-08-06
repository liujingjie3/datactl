package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.TaskEvaluationDto;
import com.zjlab.dataservice.modules.bench.model.entity.TaskEvaluation;

/**
 * BenchTask 服务接口
 */
public interface TaskEvaluationService extends IService<TaskEvaluation> {

    /**
     * 分页查询 TaskEvaluation 列表
     *
     * @param TaskEvaluationDto 查询条件 DTO
     * @return 分页结果
     */
    PageResult<TaskEvaluation> qryTaskEvaluationList(TaskEvaluationDto TaskEvaluationDto);




}
