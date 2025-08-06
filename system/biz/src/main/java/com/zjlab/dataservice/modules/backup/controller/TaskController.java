package com.zjlab.dataservice.modules.backup.controller;

import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.backup.model.dto.TaskStartDto;
import com.zjlab.dataservice.modules.backup.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/backup/task")
@Slf4j
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping("start")
    public Result start(@RequestBody @Valid TaskStartDto startDto) throws Exception {
        taskService.start(startDto.getId());
        return Result.OK();
    }

    @PostMapping("add")
    public Result add(){
        return Result.OK();
    }

    @PostMapping("edit")
    public Result edit(){
        return Result.OK();
    }

    @PostMapping("del")
    public Result del(){
        return Result.OK();
    }

    @PostMapping("pause")
    public Result pause(){
        return Result.OK();
    }

    @PostMapping("stop")
    public Result stop(){
        return Result.OK();
    }

    @PostMapping("restart")
    public Result restart(){
        return Result.OK();
    }
}
