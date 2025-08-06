package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoRead;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TodoReadMapper extends BaseMapper<TodoRead> {
    /**
     * 自定义分页
     *
     * @param page
     * @return
     */
    IPage<TodoReadDto> selectTodoReadPage(IPage<TodoReadDto> page, @Param("request") TodoReadDto request);

    /**
     * 查询一条待阅任务
     */
    TodoRead selectOneByRequest(TodoReadDto todoReadDto);
    /**
     * 关联查询任务
     * @param page
     * @param request
     * @return
     */
    IPage<TaskWebDto> selectReadInstancePage(IPage page, @Param("request") BaseTcDto request);
    /**
     * 根据流程实例编码批量更新待阅任务
     * @param list
     * @return
     */
    int updateBatchByInstanceCode(List<TodoRead> list);

}
