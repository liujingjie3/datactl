package com.zjlab.dataservice.modules.bench.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.bench.mapper.BenchDatasetMapper;
import com.zjlab.dataservice.modules.bench.model.dto.BenchDatasetListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;
import com.zjlab.dataservice.modules.bench.service.BenchDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BenchDatasetServiceImpl extends ServiceImpl<BenchDatasetMapper, BenchDataset> implements BenchDatasetService {

    @Override
    public PageResult<BenchDataset> qryBenchDatasetList(BenchDatasetListDto benchDatasetListDto) {
        // 参数校验
        checkParam(benchDatasetListDto);
        IPage<BenchDataset> page = new Page<>(benchDatasetListDto.getPageNo(), benchDatasetListDto.getPageSize());
        QueryWrapper<BenchDataset> queryWrapper = new QueryWrapper<>();
        

        // 过滤条件
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(benchDatasetListDto.getDatasetName()), BenchDataset::getDatasetName, benchDatasetListDto.getDatasetName())
                .eq(StringUtils.isNotBlank(benchDatasetListDto.getDatasetType()), BenchDataset::getDatasetType, benchDatasetListDto.getDatasetType());

        // 排序（防止 orderByField 为空）
        if (StringUtils.isNotBlank(benchDatasetListDto.getOrderByField())) {
            queryWrapper.orderByDesc(benchDatasetListDto.getOrderByField());
        } else {
            queryWrapper.orderByDesc("update_time"); // 默认按照 ID 倒序
        }

        // 查询数据
        IPage<BenchDataset> resultPage = baseMapper.selectPage(page, queryWrapper);
        List<BenchDataset> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        //整理返回结果格式
        PageResult<BenchDataset> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;

    }

    @Override
    public BenchDataset qryBenchDatasetDetail(Integer id) throws Exception {
        // 根据 ID 查询数据集详情
        BenchDataset benchDataset = baseMapper.selectById(id);
        if (benchDataset == null) {
            throw new Exception("数据集不存在");
        }
        return benchDataset;
    }

    @Override
    public BenchDataset qryDetailByName(String name) {
        // 根据数据集名称查询数据集
        return baseMapper.selectOne(
                new QueryWrapper<BenchDataset>().lambda().eq(BenchDataset::getDatasetName, name)
        );
    }

    @Override
    public String addBenchDataset(BenchDataset dataset) throws Exception {
        // 判断是否已存在相同数据集名称
        BenchDataset existingDataset = qryDetailByName(dataset.getDatasetName());
        if (existingDataset != null) {
            throw new Exception("数据集名称已存在");
        }

        // 插入新数据集
        boolean result = save(dataset);
        if (result) {
            return "数据集添加成功，ID：" + dataset.getId();
        } else {
            throw new Exception("数据集添加失败");
        }
    }

    @Override
    public String editBenchDataset(BenchDataset dataset) throws Exception {
        // 判断数据集是否存在
        BenchDataset existingDataset = baseMapper.selectById(dataset.getId());
        if (existingDataset == null) {
            throw new Exception("数据集不存在");
        }

        // 更新数据集
        boolean result = updateById(dataset);
        if (result) {
            return "数据集编辑成功，ID：" + dataset.getId();
        } else {
            throw new Exception("数据集编辑失败");
        }

    }

    @Override
    public Integer delBenchDataset(Integer id) throws Exception {
        // 判断数据集是否存在
        BenchDataset existingDataset = baseMapper.selectById(id);
        if (existingDataset == null) {
            throw new Exception("数据集不存在");
        }

        // 逻辑删除数据集
        existingDataset.setDelFlag(1); // 设置为已删除
        boolean result = updateById(existingDataset);
        if (result) {
            return 1;  // 删除成功
        } else {
            return 0;  // 删除失败
        }

    }

    /**
     * 校验参数
     */
    private void checkParam(BenchDatasetListDto benchDatasetListDto) {
        benchDatasetListDto.setPageNo(Optional.ofNullable(benchDatasetListDto.getPageNo()).orElse(1));
        benchDatasetListDto.setPageSize(Optional.ofNullable(benchDatasetListDto.getPageSize()).orElse(10));
        String field = benchDatasetListDto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            benchDatasetListDto.setOrderByField("update_time");
        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            benchDatasetListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(benchDatasetListDto.getOrderByType())) {
            benchDatasetListDto.setOrderByType("desc");
        }
    }
    
}
