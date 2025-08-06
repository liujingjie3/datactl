package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.service.TcFlowTypeService;
import com.zjlab.dataservice.modules.tc.service.TcTemplateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tc/flowtype")
public class TcFlowtypeController {
    @Autowired
    private TcFlowTypeService tcFlowTypeService;

    /**
     * 流程类型信息表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取全部服务类别", notes = "无参数")
    public Result<PageResult<FlowTypeDto>> page() {
        PageResult<FlowTypeDto> result = tcFlowTypeService.findAllTypeName();
        return Result.ok(result);
    }

}

