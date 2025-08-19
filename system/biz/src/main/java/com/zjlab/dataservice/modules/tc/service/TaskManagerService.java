package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListPageVO;

/**
 * 任务相关服务
 */
public interface TaskManagerService {
    /**
     * 分页查询任务列表
     */
    TaskManagerListPageVO listTasks(TaskManagerListQuery query);
}
