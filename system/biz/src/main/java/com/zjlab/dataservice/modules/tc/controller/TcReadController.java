package com.zjlab.dataservice.modules.tc.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoRead;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import com.zjlab.dataservice.modules.tc.service.TcReadService;
import com.zjlab.dataservice.modules.tc.service.TcTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tc/read")
public class TcReadController {

    @Autowired
    private TcReadService readService;

    // 获取所有任务实例
    @PostMapping("/list")
    public Result<PageResult<TodoReadDto>> getAllReads(@RequestBody @Valid TodoReadDto todoReadDto) {
        PageResult<TodoReadDto> result = readService.selectTodoReadPage(todoReadDto);
        return Result.ok(result);
    }

    // 待阅任务创建
    @PostMapping("/add")
    public Result<List<TodoReadDto>> addTcRead(@RequestBody @Valid List<TodoReadDto> todoReadDto) {
        List<TodoReadDto> result = readService.createReads(todoReadDto);
        return Result.ok(result);
    }

    // 修改待阅任务
    @PostMapping("/edit")
    public Result<TodoReadUpdateDto> updateTcRead(@RequestBody TodoReadUpdateDto readUpdateDto) {
        TodoReadUpdateDto result = readService.batchUpdateReads(readUpdateDto);
        return Result.ok(result);
    }


    // 分页查询待阅任务
    @PostMapping("/web")
    public Result<PageResult<TaskWebDto>> qryInstanceDetail(@RequestBody @Valid BaseTcDto basePage) {
        PageResult<TaskWebDto> result = readService.findReadInstancePage(basePage);
        return Result.ok(result);
    }

    // 查询用户待阅任务
    @PostMapping("/user")
    public Result<PageResult<TodoReadDto>> queryByUserId(@RequestBody @Valid BaseTcDto basePage) {
        PageResult<TodoReadDto> result = readService.findByUserIdPage(basePage);
        return Result.ok(result);
    }

    // 批量纠正待阅任务
    @PostMapping("/audit")
    public Result<Boolean> batchAuditTasks(@RequestBody @Valid List<TodoRead> list) {
        boolean result = readService.updateBatchInstanceCode(list);
        return Result.ok(result);
    }

    // 根据流程编码查询任务
    @PostMapping("/flow")
    public Result<List<TodoReadDto>> queryByInstance(@RequestBody @Valid String instanceCode) {
        List<TodoReadDto> result = readService.findByInstanceCode(instanceCode);
        return Result.ok(result);
    }


}

