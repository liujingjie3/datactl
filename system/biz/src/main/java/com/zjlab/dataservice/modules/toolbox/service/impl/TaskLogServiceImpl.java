package com.zjlab.dataservice.modules.toolbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.toolbox.mapper.TaskLogMapper;
import com.zjlab.dataservice.modules.toolbox.model.TaskLog;
import com.zjlab.dataservice.modules.toolbox.po.TaskLogPo;
import com.zjlab.dataservice.modules.toolbox.service.TaskLogService;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogMapper, TaskLogPo> implements TaskLogService {
    @Override
    public List<TaskLogPo> queryTaskLogById(String taskId, String fileId) {
        LambdaQueryWrapper<TaskLogPo> query = new LambdaQueryWrapper<>();
        query.eq(TaskLogPo::getTaskId, taskId);
        query.eq(TaskLogPo::getFileId, fileId);
        List<TaskLogPo> taskLogPos = Optional.ofNullable(baseMapper.selectList(query)).orElse(new ArrayList<>());

//        针对没跑过的数据
        if (taskLogPos.isEmpty()) {
            LambdaQueryWrapper<TaskLogPo> queryWithOutTaskId = new LambdaQueryWrapper<>();
            queryWithOutTaskId.eq(TaskLogPo::getFileId, fileId);
            taskLogPos = Optional.ofNullable(baseMapper.selectList(queryWithOutTaskId)).orElse(new ArrayList<>());
        }
        return taskLogPos;
    }
}
