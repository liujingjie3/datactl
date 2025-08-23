package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;

/**
 * 任务相关服务
 */
public interface TcTaskManagerService {
    /**
     * 创建任务
     */
    Long createTask(TaskManagerCreateDto dto);
    /**
     * 分页查询任务列表
     */
    PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query);

    /**
     * 取消任务
     * @param taskId 任务ID
     */
    void cancelTask(Long taskId);

    /**
     * 编辑任务
     * @param dto 编辑参数
     */
    void editTask(TaskManagerEditDto dto);
}
