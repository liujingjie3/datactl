package com.zjlab.dataservice.modules.toolbox.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.modules.toolbox.dto.FlowTemplateDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.service.FlowTemplateService;
import com.zjlab.dataservice.modules.toolbox.vo.FlowTemplateVo;
import com.zjlab.dataservice.modules.toolbox.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/toolbox")
@Slf4j
public class TemplateController {

    @Resource
    private FlowTemplateService flowTemplateService;

    @PostMapping("listPublicTemplate")
    public Result<PageResult<FlowTemplateVo>> listPublicTemplate(@RequestBody(required = false) @Valid ToolBoxDto toolBoxDto) {
        if (toolBoxDto == null) {
            toolBoxDto = new ToolBoxDto();
        }
        PageResult<FlowTemplateVo> result = flowTemplateService.listPublicTemplate(toolBoxDto);
        return Result.ok(result);
    }

    @PostMapping("listMyTemplate")
    public Result<PageResult<FlowTemplateVo>> listMyTemplate(@RequestBody(required = false) @Valid ToolBoxDto toolBoxDto) {
        if (toolBoxDto == null) {
            toolBoxDto = new ToolBoxDto();
        }
        PageResult<FlowTemplateVo> result = flowTemplateService.listMyTemplate(toolBoxDto);
        return Result.ok(result);
    }

    @PostMapping("listAllTemplate")
    public Result<PageResult<FlowTemplateVo>> listAllTemplate(@RequestBody(required = false) @Valid ToolBoxDto toolBoxDto) {
        if (toolBoxDto == null) {
            toolBoxDto = new ToolBoxDto();
        }
        PageResult<FlowTemplateVo> result = flowTemplateService.listAllTemplate(toolBoxDto);
        return Result.ok(result);
    }

    @PostMapping("createTemplate")
    public Result<FlowTemplateVo> createTemplate(@RequestBody(required = false) @Valid FlowTemplateDto flowTemplateDto) {
        FlowTemplateVo result = flowTemplateService.createTemplate(flowTemplateDto);
        if (result.getTemplateId() != null) {
            return Result.ok(result);
        } else {
            return Result.error(ResultCode.DUPLICATE_TEMPLATE_NAME.getCode(), ResultCode.DUPLICATE_TEMPLATE_NAME.getMessage());
        }
    }

    @PostMapping("updateTemplate")
    public Result<FlowTemplateVo> updateTemplate(@RequestBody(required = false) @Valid FlowTemplateDto flowTemplateDto) {
        if (flowTemplateDto == null) {
            flowTemplateDto = new FlowTemplateDto();
        }
        FlowTemplateVo result = flowTemplateService.updateTemplateById(flowTemplateDto);
        return Result.ok(result);
    }

    @PostMapping("queryTemplateById")
    public Result<FlowTemplateVo> queryTemplateById(@RequestBody(required = false) @Valid FlowTemplateDto flowTemplateDto) {
        FlowTemplateVo result = flowTemplateService.queryTemplateById(flowTemplateDto);
        if (result.getTemplateId() != null) {
            return Result.ok(result);
        } else {
            return Result.error(ResultCode.QUERY_TEMPLATE_FAIL.getCode(), ResultCode.QUERY_TEMPLATE_FAIL.getMessage());
        }
    }

}
