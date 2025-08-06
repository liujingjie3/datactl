package com.zjlab.dataservice.modules.tc.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import com.zjlab.dataservice.modules.tc.service.TcInstanceService;
import com.zjlab.dataservice.modules.tc.service.TcTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tc/task")
public class TcTaskController {

    @Autowired
    private TcTaskService taskService;

    // 获取所有任务实例
    @PostMapping("/list")
    public Result<PageResult<TodoTaskDto>> getAllTasks(@RequestBody @Valid TodoTaskQueryDto taskQueryDto) {
        PageResult<TodoTaskDto> result = taskService.selectTodoTaskPage(taskQueryDto);
        return Result.ok(result);
    }

    // 创建任务实例
    @PostMapping("/add")
    public Result<List<TodoTaskDto>> addTcTask(@RequestBody @Valid List<TodoTaskDto> todoTaskDto) {
        List<TodoTaskDto> result = taskService.createTasks(todoTaskDto);
        return Result.ok(result);
    }

    // 查询任务详情
    @PostMapping("/detail")
    public Result<List<TodoTaskDto>> qryInstanceDetail(@RequestBody @Valid TodoTaskQueryDto todoTaskQueryDto) {
        List<TodoTaskDto> result = taskService.getList(todoTaskQueryDto);
        return Result.ok(result);
    }

    // 查询特定的task
    @PostMapping("/detail/code")
    public Result<TodoTaskDto> qryInstanceDetail(@RequestBody String taskCode) {
        TodoTaskDto result = taskService.findOneByCodeTaskId(taskCode);
        return Result.ok(result);
    }

    // 更新流程实例
    @PostMapping("/edit")
    public Result<TodoTaskUpdateDto> updateTcTask(@RequestBody TodoTaskUpdateDto taskUpdateDto) {
        TodoTaskUpdateDto result = taskService.batchUpdateTasks(taskUpdateDto);
        return Result.ok(result);
    }

    // 根据流程编码查询任务
    @PostMapping("/flow")
    public Result<List<TodoTaskDto>> queryByInstance(@RequestBody @Valid String instanceCode) {
        List<TodoTaskDto> result = taskService.findByInstanceCode(instanceCode);
        return Result.ok(result);
    }

    // 查询用户待办任务
    @PostMapping("/user")
    public Result<PageResult<TodoTaskDto>> queryByUserId(@RequestBody @Valid BaseTcDto basePage) {
        PageResult<TodoTaskDto> result = taskService.findByUserIdPage(basePage);
        return Result.ok(result);
    }

    //web展示全部待办任务
    @PostMapping("/web")
    public Result<PageResult<TaskWebDto>> queryByWeb(@RequestBody @Valid BaseTcDto basePage) {
        PageResult<TaskWebDto> result = taskService.findTaskInstancePage(basePage);
        return Result.ok(result);
    }

    // 批量纠正待办任务
    @PostMapping("/audit")
    public Result<Boolean> batchAuditTasks(@RequestBody @Valid List<TodoTask> list) {
        boolean result = taskService.updateBatchInstanceCode(list);
        return Result.ok(result);
    }

    // 统计个人待办任务
    @PostMapping("/num")
    public Result<Long> calculateUserTasks(@RequestBody BaseTcDto tcDto) {
        Long result = taskService.findByUserCount(tcDto);
        return Result.ok(result);
    }

    // 提醒的待办任务
    @PostMapping("/remind")
    public Result<PageResult<TaskRemindDto>> queryReminTasks(@RequestBody @Valid TaskRemindDto taskRemindDto) {
        PageResult<TaskRemindDto> result = taskService.getRemindTasks(taskRemindDto);
        return Result.ok(result);
    }


}

