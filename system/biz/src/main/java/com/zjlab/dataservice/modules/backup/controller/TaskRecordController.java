package com.zjlab.dataservice.modules.backup.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.backup.model.dto.TaskRecordDto;
import com.zjlab.dataservice.modules.backup.model.vo.TaskRecordVo;
import com.zjlab.dataservice.modules.backup.service.TaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/backup/taskRecord")
@Slf4j
public class TaskRecordController {

    @Resource
    private TaskRecordService recordService;

    /**
     * 获取任务执行记录
     * @param recordDto
     * expireDay 过期天数，获取指定日期内的数据
     * @return
     */
    @PostMapping("list")
    public Result<PageResult<TaskRecordVo>> qryTaskRecordList(@RequestBody TaskRecordDto recordDto){
        PageResult<TaskRecordVo> recordList = recordService.qryTaskRecordList(recordDto);
        return Result.OK(recordList);
    }

}
