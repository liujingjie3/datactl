package com.zjlab.dataservice.modules.bench.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.mapper.TaskEvaluationMapper;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.dto.TaskEvaluationDto;
import com.zjlab.dataservice.modules.bench.model.entity.TaskEvaluation;
import com.zjlab.dataservice.modules.bench.service.TaskEvaluationService;
import com.zjlab.dataservice.modules.system.service.ISysDictItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * TaskEvaluation 服务实现类
 */
@Service
public class TaskEvaluationServiceImpl extends ServiceImpl<TaskEvaluationMapper, TaskEvaluation> implements TaskEvaluationService {

    @Resource
    private ISysDictItemService sysDictItemService;

    @Override
    public PageResult<TaskEvaluation> qryTaskEvaluationList(TaskEvaluationDto taskEvaluationDto) {
        // 参数校验
        checkParam(taskEvaluationDto);
        IPage<TaskEvaluation> page = new Page<>(1, 10000);

        // 查询数据，SQL 已经返回 teamName
        IPage<TaskEvaluation> resultPage = baseMapper.selectByModelAndCapability(page, taskEvaluationDto);
        List<TaskEvaluation> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        // 返回分页结果
        PageResult<TaskEvaluation> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;

    }

    /**
     * 校验参数
     */
    private void checkParam(TaskEvaluationDto taskEvaluationDto) {
        taskEvaluationDto.setPageNo(Optional.ofNullable(taskEvaluationDto.getPageNo()).orElse(1));
        taskEvaluationDto.setPageSize(Optional.ofNullable(taskEvaluationDto.getPageSize()).orElse(10));
        String field = taskEvaluationDto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            taskEvaluationDto.setOrderByField("harmonic_mean_score");
        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            taskEvaluationDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(taskEvaluationDto.getOrderByType())) {
            taskEvaluationDto.setOrderByType("desc");
        }
    }

}
