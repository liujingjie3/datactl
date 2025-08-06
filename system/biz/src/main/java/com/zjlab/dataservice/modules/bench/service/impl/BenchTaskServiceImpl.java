package com.zjlab.dataservice.modules.bench.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.mapper.BenchTaskMapper;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.service.BenchTaskService;
import com.zjlab.dataservice.modules.system.service.ISysDictItemService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * BenchTask 服务实现类
 */
@Service
public class BenchTaskServiceImpl extends ServiceImpl<BenchTaskMapper, BenchTask> implements BenchTaskService {

    @Resource
    private ISysDictItemService sysDictItemService;

    @Override
    public PageResult<BenchTask> qryBenchTaskList(BenchTaskListDto benchTaskListDto) {
        // 参数校验
        checkParam(benchTaskListDto);
        IPage<BenchTask> page = new Page<>(1, 100);

        // 查询数据，SQL 已经返回 teamName
        IPage<BenchTask> resultPage = baseMapper.qryBenchTaskList(page, benchTaskListDto);
        List<BenchTask> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        // 返回分页结果
        PageResult<BenchTask> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;

    }

    /**
     * 校验参数
     */
    private void checkParam(BenchTaskListDto benchTaskListDto) {
        benchTaskListDto.setPageNo(Optional.ofNullable(benchTaskListDto.getPageNo()).orElse(1));
        benchTaskListDto.setPageSize(Optional.ofNullable(benchTaskListDto.getPageSize()).orElse(10));
        String field = benchTaskListDto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            benchTaskListDto.setOrderByField("update_time");
        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            benchTaskListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(benchTaskListDto.getOrderByType())) {
            benchTaskListDto.setOrderByType("desc");
        }
    }

}
