package com.zjlab.dataservice.modules.bench.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.bench.model.dto.BenchScoreListDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchTaskListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTask;
import com.zjlab.dataservice.modules.bench.model.entity.ResultTask;
import com.zjlab.dataservice.modules.bench.service.BenchScoreService;
import com.zjlab.dataservice.modules.bench.service.BenchTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/bench/score")
public class BenchScoreController {

    @Autowired
    private BenchScoreService benchScoreService;

    @PostMapping("/task")
    public Result<PageResult<ResultTask>> getBenchScoresByTask(@RequestBody @Valid BenchScoreListDto benchScoreListDto) {
        PageResult<ResultTask> result = benchScoreService.qryBenchScoreList(benchScoreListDto);
        return Result.ok(result);
    }

}
