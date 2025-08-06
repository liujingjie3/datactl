package com.zjlab.dataservice.modules.toolbox.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.modules.toolbox.dto.TaskDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.service.ToolboxTaskService;
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
public class ToolboxTaskController {

    @Resource
    private ToolboxTaskService toolboxTaskService;

    @PostMapping("listTask")
    public Result<PageResult<TaskVo>> listTasksByCondition(@RequestBody ToolBoxDto toolBoxDto) {
        PageResult<TaskVo> result = toolboxTaskService.listTaskByCondition(toolBoxDto);
        return Result.ok(result);
    }

    @PostMapping("createTask")
    public Result<TaskVo> createTask(@RequestBody(required = false) @Valid TaskDto taskDto) {
        TaskVo taskVo = toolboxTaskService.createTask(taskDto);
        if (taskVo.getTaskId() != null) {
            return Result.ok(taskVo);
        } else {
            return Result.error(ResultCode.DUPLICATE_TASK_NAME.getCode(), ResultCode.DUPLICATE_TASK_NAME.getMessage());
        }
    }

    @PostMapping("queryTask")
    public Result<TaskVo> queryTask(@RequestBody(required = false) @Valid TaskDto taskDto) {
        TaskVo taskVo = toolboxTaskService.queryTask(taskDto.getTaskId());
        return Result.ok(taskVo);
    }

    @PostMapping("delete")
    public Result<Boolean> deleteTask(@RequestBody(required = false) @Valid TaskDto taskDto) {
        boolean result = toolboxTaskService.deleteTaskById(taskDto.getTaskId());
        return Result.ok(result);
    }

    @PostMapping("update")
    public Result<TaskVo> updateTask(@RequestBody(required = false) @Valid TaskDto taskDto) {
        TaskVo taskVo = toolboxTaskService.updateTaskById(taskDto);
        if (taskVo.getTaskId() != null) {
            return Result.ok(taskVo);
        } else {
            return Result.error(ResultCode.UPDATE_TASK_FAIL.getCode(), ResultCode.UPDATE_TASK_FAIL.getMessage());
        }
    }

    @PostMapping("startTask")
    public Result<TaskVo> startTask(@RequestBody(required = false) @Valid TaskDto taskDto) {
        TaskVo taskVo = toolboxTaskService.startTask(taskDto);
        if (taskVo.getTaskId() != null) {
            return Result.ok(taskVo);
        } else {
            return Result.error(ResultCode.START_TASK_FAIL.getCode(), ResultCode.START_TASK_FAIL.getMessage());
        }
    }


}
