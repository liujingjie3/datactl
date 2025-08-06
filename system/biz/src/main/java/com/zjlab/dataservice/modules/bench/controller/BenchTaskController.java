package com.zjlab.dataservice.modules.bench.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.service.BenchTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/bench/task")
public class BenchTaskController {

    @Autowired
    private BenchTaskService benchTaskService;

    @PostMapping("/all")
    public Result<PageResult<BenchTask>> getBenchTasks(@RequestBody @Valid BenchTaskListDto benchTaskListDto) {
        PageResult<BenchTask> result = benchTaskService.qryBenchTaskList(benchTaskListDto);
        return Result.ok(result);
    }

}
