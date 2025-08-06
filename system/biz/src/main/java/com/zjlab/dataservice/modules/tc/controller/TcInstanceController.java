package com.zjlab.dataservice.modules.tc.controller;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.BaseTcDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoInstanceDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoInstanceUpdateDto;
import com.zjlab.dataservice.modules.tc.service.TcInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tc/instance")
public class TcInstanceController {

    @Autowired
    private TcInstanceService instanceService;

    // 获取所有流程实例
    @PostMapping("/list")
    public Result<PageResult<TodoInstanceDto>> getAllInstances(@RequestBody @Valid TodoInstanceDto todoInstanceDto) {
        PageResult<TodoInstanceDto> result = instanceService.selectTodoInstancePage(todoInstanceDto);
        return Result.ok(result);
    }

    // 创建流程实例
    @PostMapping("/add")
    public Result<TodoInstanceDto> addTcInstance(@RequestBody @Valid TodoInstanceDto todoInstanceDto) {
        TodoInstanceDto result = instanceService.createInstance(todoInstanceDto);
        return Result.ok(result);
    }

    /**
     * 查询流程详情
     */
    @PostMapping("/detail")
    public Result<TodoInstanceDto> qryInstanceDetail(@RequestBody @Valid TodoInstanceDto todoInstanceDto) {
        TodoInstanceDto result = instanceService.findOneByInstance(todoInstanceDto);
        return Result.ok(result);
    }

    // 更新流程实例
    @PostMapping("/edit")
    public Result<PageResult<TodoInstanceUpdateDto>> editInstanceBatch(@RequestBody List<TodoInstanceUpdateDto> instanceUpdateVOS) {
        PageResult<TodoInstanceUpdateDto> result = instanceService.batchUpdateInstance(instanceUpdateVOS);
        return Result.ok(result);
    }

    // 分页获取发起人的流程
    @PostMapping("/user")
    public Result<PageResult<TodoInstanceDto>> queryUserInstance(@RequestBody @Valid BaseTcDto baseTcDto) {
        PageResult<TodoInstanceDto> result = instanceService.findByUserIdPage(baseTcDto);
        return Result.ok(result);
    }
}

