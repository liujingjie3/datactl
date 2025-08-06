package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TodoTaskMapper extends BaseMapper<TodoTask> {
    /**
     * 自定义分页
     *
     * @param page
     * @return
     */
    IPage<TodoTaskDto> selectTodoTaskPage(IPage<TodoTaskQueryDto> page, @Param("request") TodoTaskQueryDto request);

    /**
     * 选择一条数据
     *
     */
    TodoTask selectOneByRequest(@Param("request") TodoTaskDto request);
    /**
     * 关联查询任务
     * @param page
     * @return
     */
    IPage<TaskWebDto> selectTaskInstancePage(IPage page, @Param("request") BaseTcDto request);

    /**
     * 指定参数查询返回一条记录
     * @param task
     * @return
     */
    TodoTask selectByInstanceCodeAndTaskId(@Param("task") TodoTask task);

    /**
     * 根据流程实例编码批量更新待办任务
     * @param list
     * @return
     */
    int updateBatchByInstanceCode(@Param("list") List<TodoTask> list);

    // 流程实例查询
    List<TodoTaskDto> selectTodoTaskByflow(@Param("instanceCode") String instanceCode);

    /**
     * 统计运行中的待办任务统计
     * @return
     */
    Long selectByUserCount(@Param("request") BaseTcDto tcDto);

    /**
     * 统计实例的待办任务统计
     * @param instanceCode 实咧编码
     * @return
     */
    Long countByInstanceCode(@Param("instanceCode") String instanceCode);

    /**
     * 待办提醒统计数据分页
     * @param page
     * @return
     */
    IPage<TaskRemindDto> selectCountByUserPage(IPage<TaskRemindDto> page, @Param("request") TaskRemindDto request);

}
