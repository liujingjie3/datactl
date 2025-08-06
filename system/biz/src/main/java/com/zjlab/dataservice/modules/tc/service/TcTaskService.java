package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TcTaskService extends IService<TodoTask> {

    /**
     * 自定义分页
     *
     */
    PageResult<TodoTaskDto> selectTodoTaskPage(TodoTaskQueryDto taskQueryDto);


    /**
     * 批量创建待办任务
     * @return
     */
    List<TodoTaskDto> createTasks(List<TodoTaskDto> todoTaskDto);

    /**
     * 更新待办任务状态
     * @return
     */
    TodoTaskUpdateDto batchUpdateTasks(TodoTaskUpdateDto taskUpdateDto);

    /**
     * 用户分页查询待办任务
     * @param basePage
     * @return
     */
    PageResult<TodoTaskDto> findByUserIdPage(BaseTcDto basePage);

    /**
     * 用户分页查询待办任务
     * @param basePage
     * @return
     */
    PageResult<TaskWebDto> findTaskInstancePage(BaseTcDto basePage);


    /**
     * 根据流程实例编码批量更新待办任务
     * @param list
     * @return
     */
    boolean updateBatchInstanceCode(List<TodoTask> list);

    /**
     * 查询待办任务
     * @param todoTaskQueryDto
     * @return
     */
    List<TodoTaskDto> getList(TodoTaskQueryDto todoTaskQueryDto);

    /**
     * 通过实例编码查询运行中的待办任务
     * @param instanceCode 实例编码
     * @return
     */
    List<TodoTaskDto> findByInstanceCode(@NotNull String instanceCode);

    /**
     * 查询详情
     * @param taskCode 待办任务编码
     * @return
     */
    TodoTaskDto findOneByCodeTaskId(String taskCode);

    /**
     * 统计运行中的待办任务统计
     * @return
     */
    Long findByUserCount(BaseTcDto tcDto);

    /**
     * 统计实例的待办任务统计
     * @param instanceCode 实咧编码
     * @return
     */
    Long countByInstanceCode(String instanceCode);

    /**
     * 待办提醒统计数据分页
     * @return
     */
    PageResult<TaskRemindDto> getRemindTasks(TaskRemindDto taskRemindDto);

}
