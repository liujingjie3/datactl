package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/** 任务列表接口 */
@RestController
@RequestMapping("/tc/task")
@Api(tags="任务管理")
public class TcTaskManagerController {

    @Autowired
    private TcTaskManagerService taskManagerService;

    @GetMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value="任务列表", notes="分页查询任务列表")
    public Result<PageResult<TaskManagerListItemVO>> list(TaskManagerListQuery query) {
        return Result.ok(taskManagerService.listTasks(query));
    }
}
