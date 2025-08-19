package com.zjlab.dataservice.modules.task.service;

import com.zjlab.dataservice.modules.task.model.dto.TaskListQuery;
import com.zjlab.dataservice.modules.task.model.vo.TaskListPageVO;

/**
 * 任务相关服务
 */
public interface TaskService {
    /**
     * 分页查询任务列表
     */
    TaskListPageVO listTasks(TaskListQuery query);
}
