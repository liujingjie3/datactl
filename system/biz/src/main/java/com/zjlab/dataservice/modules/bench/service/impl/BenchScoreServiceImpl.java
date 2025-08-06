package com.zjlab.dataservice.modules.bench.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;

import com.zjlab.dataservice.modules.bench.mapper.BenchScoreMapper;
import com.zjlab.dataservice.modules.bench.model.dto.BenchScoreListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchScore;
import com.zjlab.dataservice.modules.bench.model.entity.ResultTask;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;
import com.zjlab.dataservice.modules.bench.service.BenchScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BenchScore 服务实现类
 */
@Service
public class BenchScoreServiceImpl extends ServiceImpl<BenchScoreMapper, BenchScore> implements BenchScoreService {

    @Transactional
    @Override
    public PageResult<ResultTask> qryBenchScoreList(BenchScoreListDto benchScoreListDto) {
        IPage<BenchScore> page = new Page<>(benchScoreListDto.getPageNo(), benchScoreListDto.getPageSize());
        // 获取数据
        List<BenchScore> allScores = baseMapper.selectByModelAndTask(page, benchScoreListDto);

        // 分组：根据 model_id 聚合得分，并将 dataset_name 和 score 嵌套在 children 中
        Map<Integer, List<BenchScore>> groupedScores = allScores.stream()
                .collect(Collectors.groupingBy(BenchScore::getModelId));

        // 格式化结果
        List<ResultTask> listRecords = new ArrayList<>();
        for (Map.Entry<Integer, List<BenchScore>> entry : groupedScores.entrySet()) {
            ResultTask resultTask = new ResultTask();
            resultTask.setModelId(entry.getKey());
            // 使用调和平均数计算得分
            resultTask.setScore(calculateHarmonicMean(entry.getValue()));

            // 按 dataset_name 分组并存入 children
            List<BenchScore> children = entry.getValue().stream()
                    .map(score -> new BenchScore(
                            score.getDatasetName(),
                            score.getScore(),
                            score.getModelId(),
                            score.getTaskIdentifier(),
                            score.getMetric(),
                            score.getLastUpdated()
                    ))
                    .collect(Collectors.toList());
            resultTask.setChildren(children);

            listRecords.add(resultTask);
        }

        // 返回分页结果
        PageResult<ResultTask> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;

    }

    // 计算调和平均数
    private double calculateHarmonicMean(List<BenchScore> scores) {
        int n = scores.size();
        if (n == 0) {
            return 0; // 防止空列表导致除以0
        }
        double sumOfReciprocals = scores.stream()
                .mapToDouble(score -> 1.0 / score.getScore())
                .sum();
        return n / sumOfReciprocals;
    }


}
