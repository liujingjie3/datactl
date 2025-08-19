package com.zjlab.dataservice.modules.tc.controller;

import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.TaskListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskListPageVO;
import com.zjlab.dataservice.modules.tc.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/** 任务列表接口 */
@RestController
@RequestMapping("/task")
public class TcTaskManagerController {

    @Resource
    private TaskService taskService;

    @GetMapping("/list")
    public Result<TaskListPageVO> list(TaskListQuery query) {
        TaskListPageVO page = taskService.listTasks(query);
        return Result.ok(page);
    }
}
