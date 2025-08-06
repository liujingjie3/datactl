package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.entity.BenchCapability;

import java.util.List;

public interface BenchCapabilityService extends IService<BenchCapability> {
    /**
     * 查询全部 BenchCapability 列表
     *
     * @return 全部结果
     */
    PageResult<BenchCapability> qryBenchAllCapability();


}
