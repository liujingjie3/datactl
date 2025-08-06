package com.zjlab.dataservice.modules.bench.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelAddDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelEditDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchModel;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;
import com.zjlab.dataservice.modules.bench.service.BenchModelService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * BenchModel 控制层，提供 CRUD 和分页接口
 */
@RestController
@RequestMapping("/bench/model")
public class BenchModelController {

    @Resource
    private BenchModelService benchModelService;

    /**
     * 分页查询 BenchModel 列表
     */
    @PostMapping("/list")
    public Result<PageResult<?>> qryBenchModelList(@RequestBody @Valid BenchModelListDto benchModelListDto) {
        PageResult<?> result = benchModelService.qryBenchModelList(benchModelListDto);
        return Result.ok(result);
    }

    /**
     * 根据 ID 查询模型详情
     */
    @GetMapping("/detail/{id}")
    public Result<BenchModel> qryBenchModelDetail(@PathVariable Integer id) {
        try {
            BenchModel detailVo = benchModelService.qryBenchModelDetail(id);
            return Result.ok(detailVo);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据模型名称查询模型信息
     */
    @GetMapping("/detailByName")
    public Result<BenchModel> qryDetailByName(@RequestParam String name) {
        BenchModel benchModel = benchModelService.qryDetailByName(name);
        return benchModel != null ? Result.ok(benchModel) : Result.error("模型不存在");
    }

    /**
     * 新增模型
     */
    @PostMapping("/add")
    public Result<String> addBenchModel(@RequestBody @Valid BenchModelAddDto addDto) {
        try {
            String result = benchModelService.addBenchModel(addDto);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 编辑模型
     */
    @PostMapping("/edit")
    public Result<String> editBenchModel(@RequestBody @Valid BenchModelEditDto editDto) {
        try {
            String result = benchModelService.editBenchModel(editDto);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("编辑失败: " + e.getMessage());
        }
    }

    /**
     * 删除模型（逻辑删除）
     */
    @PostMapping("/delete/{id}")
    public Result<Integer> delBenchModel(@PathVariable Integer id) {
        try {
            Integer result = benchModelService.delBenchModel(id);
            return result > 0 ? Result.ok(result) : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

}
