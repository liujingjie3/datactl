package com.zjlab.dataservice.modules.tc.controller;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.bench.model.dto.BenchDatasetListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.service.TcTemplateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/tc/template")
public class TcTemplateController {
    @Autowired
    private TcTemplateService tcTemplateService;

    /**
     * 自定义分页 实例模板信息表
     */
    @PostMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页", notes = "传入basePage")
    public Result<PageResult<TodoTemplateDto>> page(@Valid @RequestBody TodoTemplateQueryDto basePage) {
        PageResult<TodoTemplateDto> result = tcTemplateService.qryTemplateList(basePage);
        return Result.ok(result);
    }

    /**
     * 详情
     */
    @PostMapping("/detail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "详情", notes = "传入todoTemplateQueryVO")
    public Result<TodoTemplateDto> detail(@Valid @RequestBody TodoTemplateQueryDto todoTemplateQueryVO) {
        try {
            TodoTemplateDto todoTemplateDto = tcTemplateService.findOne(todoTemplateQueryVO);
            return Result.ok(todoTemplateDto);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 新增 实例模板信息表
     */
    @PostMapping(value ="/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "新增或修改", notes = "传入templateVO")
    public Result<TodoTemplateDto> save(@RequestParam("file") MultipartFile file,
                                        @RequestPart("template") TodoTemplateDto todoTemplateDto) {
        try {
            TodoTemplateDto todoTemplate = tcTemplateService.submit(file,todoTemplateDto);
            return Result.OK("新增或修改成功", todoTemplate);
        } catch (Exception e) {
            return Result.error("新增或修改失败: " + e.getMessage());
        }
    }


    /**
     * 删除 实例模板信息表
     */
    @PostMapping("/delete")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "删除", notes = "传入todoTemplateQueryVO")
    public Result<TodoTemplateQueryDto> remove(@Valid @RequestBody TodoTemplateQueryDto todoTemplateQueryDto) {
        try {
            TodoTemplateQueryDto todoTemplateDto = tcTemplateService.deleteByCode(todoTemplateQueryDto);
            return Result.OK("删除成功", todoTemplateDto);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

}

