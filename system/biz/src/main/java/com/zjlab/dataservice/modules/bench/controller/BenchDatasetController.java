package com.zjlab.dataservice.modules.bench.controller;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.bench.model.dto.BenchDatasetListDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelEditDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;
import com.zjlab.dataservice.modules.bench.service.BenchDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bench/dataset")
public class BenchDatasetController {
    @Autowired
    private BenchDatasetService benchDatasetService;

    // 获取所有数据集
    @PostMapping("/list")
    public Result<PageResult<BenchDataset>> getAllBenchDatasets(@RequestBody @Valid BenchDatasetListDto benchDatasetListDto) {
        PageResult<BenchDataset> result = benchDatasetService.qryBenchDatasetList(benchDatasetListDto);
        return Result.ok(result);
    }

    // 根据 ID 获取数据集
    @GetMapping("/detail/{id}")
    public Result<BenchDataset> getBenchDatasetById(@PathVariable Integer id) {
        try {
            BenchDataset detailVo = benchDatasetService.qryBenchDatasetDetail(id);
            return Result.ok(detailVo);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 添加新的数据集
    @PostMapping("/add")
    public Result<String> addBenchDataset(@RequestBody BenchDataset benchDataset) {
        try {
            String result = benchDatasetService.addBenchDataset(benchDataset);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    // 更新数据集
    @PostMapping("/edit")
    public Result<String> editBenchDataset(@RequestBody BenchDataset benchDataset) {
        try {
            String result = benchDatasetService.editBenchDataset(benchDataset);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("编辑失败: " + e.getMessage());
        }
    }

    // 删除数据集
    @PostMapping("/delete/{id}")
    public Result<Integer> delBenchDataset(@PathVariable Integer id) {
        try {
            Integer result = benchDatasetService.delBenchDataset(id);
            return result > 0 ? Result.ok(result) : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}

