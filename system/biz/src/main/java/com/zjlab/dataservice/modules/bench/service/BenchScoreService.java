package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.BenchScoreListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchScore;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.model.entity.ResultTask;

/**
 * BenchScore 服务接口
 */
public interface BenchScoreService extends IService<BenchScore> {

    /**
     * 分页查询 BenchScore 列表
     *
     * @return 分页结果
     */
    PageResult<ResultTask> qryBenchScoreList(BenchScoreListDto benchScoreListDto);



}
