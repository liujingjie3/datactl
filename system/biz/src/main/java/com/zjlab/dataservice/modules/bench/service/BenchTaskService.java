package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;

/**
 * BenchTask 服务接口
 */
public interface BenchTaskService extends IService<BenchTask> {

    /**
     * 分页查询 BenchTask 列表
     *
     * @param benchTaskListDto 查询条件 DTO
     * @return 分页结果
     */
    PageResult<BenchTask> qryBenchTaskList(BenchTaskListDto benchTaskListDto);




}
