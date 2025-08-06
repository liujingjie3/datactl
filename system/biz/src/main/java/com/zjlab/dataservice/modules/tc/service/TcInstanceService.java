package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.TodoInstanceDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoInstanceUpdateDto;
import com.zjlab.dataservice.modules.tc.model.dto.BaseTcDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoInstance;

import java.util.List;

/**
 *  服务接口
 */
public interface TcInstanceService extends IService<TodoInstance> {

    /**
     * 分页查询流程实例
     *
     * @param todoInstance
     * @return
     */
    PageResult<TodoInstanceDto> selectTodoInstancePage(TodoInstanceDto todoInstance);


    /**
     * 创建实例记录
     * @param instanceVO
     * @return
     */
    TodoInstanceDto createInstance(TodoInstanceDto instanceVO);


    /**
     * 查询单条实例对象
     * @param instanceVO
     * @return
     */
    TodoInstanceDto findOneByInstance(TodoInstanceDto instanceVO);

    /**
     * 批量更新实例状态
     * @param instanceUpdateVOS
     * @return
     */
    PageResult<TodoInstanceUpdateDto> batchUpdateInstance(List<TodoInstanceUpdateDto> instanceUpdateVOS);


    /**
     * 用户流程分页查询待办任务
     * @param baseTcDto
     * @return
     */
    PageResult<TodoInstanceDto> findByUserIdPage(BaseTcDto baseTcDto);



}
