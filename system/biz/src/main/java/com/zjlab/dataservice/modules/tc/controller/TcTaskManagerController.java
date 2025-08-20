package com.zjlab.dataservice.modules.tc.controller;

import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListPageVO;
import com.zjlab.dataservice.modules.tc.service.TaskManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/** 任务列表接口 */
@RestController
@RequestMapping("/task")
@Api(tags="任务列表")
public class TcTaskManagerController {

    @Resource
    private TaskManagerService taskManagerService;

    @GetMapping("/list")
    @ApiOperation(value="任务列表", notes="分页查询任务列表")
    public Result<TaskManagerListPageVO> list(TaskManagerListQuery query) {
        TaskManagerListPageVO page = taskManagerService.listTasks(query);
        return Result.ok(page);
    }
}
