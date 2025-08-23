package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


/** 任务列表接口 */
@RestController
@RequestMapping("/tc/taskManager")
@Api(tags="任务管理")
@Validated
public class TcTaskManagerController {

    @Autowired
    private TcTaskManagerService taskManagerService;

    @GetMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value="任务列表", notes="分页查询任务列表")
    public Result<PageResult<TaskManagerListItemVO>> list(@Valid TaskManagerListQuery query) {
        return Result.ok(taskManagerService.listTasks(query));
    }

    @PostMapping("/create")
    @ApiOperationSupport(order = 0)
    @ApiOperation(value = "创建任务", notes = "创建新的任务")
    public Result<Long> create(@RequestBody @Valid TaskManagerCreateDto dto) {
        Long id = taskManagerService.createTask(dto);
        return Result.ok(id);
    }

    @PostMapping("/cancel")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value="取消任务", notes="取消指定任务")
    public Result<Void> cancel(@RequestParam Long taskId) {
        taskManagerService.cancelTask(taskId);
        return Result.ok();
    }

    @PostMapping("/edit")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "编辑任务", notes = "编辑指定任务")
    public Result<Void> edit(@RequestBody @Valid TaskManagerEditDto dto) {
        taskManagerService.editTask(dto);
        return Result.ok();
    }
}
