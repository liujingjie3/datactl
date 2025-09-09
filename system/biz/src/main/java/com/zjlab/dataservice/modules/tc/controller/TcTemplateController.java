package com.zjlab.dataservice.modules.tc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.zjlab.dataservice.modules.tc.model.vo.CommandVO;
import com.zjlab.dataservice.modules.tc.model.vo.SatellitePassesVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateCountVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateQueryListVO;
import com.zjlab.dataservice.modules.tc.service.SatellitePassesService;
import com.zjlab.dataservice.modules.tc.service.TcSatellitepassesService;
import com.zjlab.dataservice.modules.tc.service.TcTemplateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tc/template")
public class TcTemplateController {
    @Autowired
    private TcTemplateService tcTemplateService;

    @Autowired
    private SatellitePassesService satellitePassesService;

    @Autowired
    private TcSatellitepassesService tcSatellitepassesService;

    /**
     * 自定义分页 实例模板信息表
     */
    @PostMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页", notes = "传入basePage")
    public Result<PageResult<TemplateQueryListVO>> page(@RequestBody(required = false) QueryListDto basePage) {
        if (basePage == null) {
            basePage = new QueryListDto(); // 赋一个默认对象
        }
        PageResult<TemplateQueryListVO> result = tcTemplateService.qryTemplateList(basePage);
        return Result.ok(result);
    }

    /**
     * 详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "详情", notes = "传入todoTemplateQueryVO")
    public Result<TodoTemplate> detail(@PathVariable Long id) {
        TodoTemplate todoTemplate = tcTemplateService.getDetail(id);
        return Result.ok(todoTemplate);
    }

    /**
     * 新增 实例模板信息表
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "新增或修改", notes = "传入templateVO")
    public Result<TodoTemplateDto> save(@RequestParam(value = "file", required = false) MultipartFile file,
                                        @RequestPart("todoTemplateDto") String todoTemplateDtoJson) throws Exception {

        TodoTemplateDto dto = new ObjectMapper().readValue(todoTemplateDtoJson, TodoTemplateDto.class);
        TodoTemplateDto todoTemplate = tcTemplateService.submit(file, dto);
        return Result.OK("新增模版成功", todoTemplate);

    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "修改", notes = "传入templateVO")
    public Result<TodoTemplateDto> update(@RequestParam(value = "file", required = false) MultipartFile file,
                                          @RequestPart("todoTemplateDto") String todoTemplateDtoJson) throws Exception {

        TodoTemplateDto dto = new ObjectMapper().readValue(todoTemplateDtoJson, TodoTemplateDto.class);
        TodoTemplateDto todoTemplate = tcTemplateService.update(file, dto);
        return Result.OK("修改模版成功", todoTemplate);
    }


    /**
     * 删除 实例模板信息表
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "删除")
    public Result<TodoTemplateQueryDto> remove(@PathVariable Long id) {
        tcTemplateService.deleteById(id);
        return Result.ok();
    }

    /**
     * 发布
     */
    @PutMapping("/pubilsh/{id}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "编辑节点")
    public Result<Void> publish(@PathVariable Long id) {
        tcTemplateService.publish(id);
        return Result.ok();
    }

    /**
     * 详情
     */
    @GetMapping("/static")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "统计")
    public Result<TemplateCountVO> count() {
        TemplateCountVO templateCountVO = tcTemplateService.getCount();
        return Result.ok(templateCountVO);
    }

    @GetMapping("/parse/{id}")
    @ApiOperation(value = "解析指令单")
    public Result<List<CommandVO>> parse(@PathVariable Long id) throws Exception {
        List<CommandVO> commandVOList = tcTemplateService.parse(id);
        return Result.ok(commandVOList);
    }

    //卫星轨迹信息接口
    @PostMapping("/passinfo")
    @ApiOperation(value = "获取当下时刻往后三天的卫星轨道")
    public Result<List<SatellitePassesVO>> passInfo(@RequestBody @Valid PassInfoQueryDto dto) {
        List<SatellitePassesVO> satellitePassesVoList = satellitePassesService.passinfo(dto);
        return Result.ok(satellitePassesVoList);
    }

    @PostMapping("/addpassinfo")
    @ApiOperation(value = "添加到中间信息表")
    public Result addPassInfo(@RequestBody(required = false) PassInfoAddDto passInfoAddDto) {
        List<SatellitePassesDto> satellitePassesDtoList = passInfoAddDto.getSatellitePassesDtoList();
        if (satellitePassesDtoList == null || satellitePassesDtoList.isEmpty()) {
            return Result.OK("no data");
        }
        tcSatellitepassesService.addPassInfo(satellitePassesDtoList);
        return Result.ok();
    }


}

