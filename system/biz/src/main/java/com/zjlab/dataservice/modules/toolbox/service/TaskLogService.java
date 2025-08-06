package com.zjlab.dataservice.modules.toolbox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.toolbox.po.TaskLogPo;

import java.util.List;

public interface TaskLogService extends IService<TaskLogPo> {

    List<TaskLogPo> queryTaskLogById(String taskId, String fileId);
}
