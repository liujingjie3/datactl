package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoRead;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TcReadService extends IService<TodoRead> {

    /**
     * 自定义分页
     *
     */
    PageResult<TodoReadDto> selectTodoReadPage(TodoReadDto todoReadDto);


    /**
     * 批量创建待阅任务
     * @return
     */
    List<TodoReadDto> createReads(List<TodoReadDto> todoReadDto);

    /**
     * 批量更新待阅任务状态
     * @return
     */
    TodoReadUpdateDto batchUpdateReads(TodoReadUpdateDto readUpdateDto);


    /**
     * 分页查询待阅任务
     * @param basePage
     * @return
     */
    PageResult<TaskWebDto> findReadInstancePage(BaseTcDto basePage);

    
    /**
     * 用户分页查询待阅任务
     * @param basePage
     * @return
     */
    PageResult<TodoReadDto> findByUserIdPage(BaseTcDto basePage);


    /**
     * 根据流程实例编码批量更新待阅任务
     * @param list
     * @return
     */
    boolean updateBatchInstanceCode(List<TodoRead> list);


    /**
     * 通过实例编码查询运行中的待阅任务
     * @param instanceCode 实例编码
     * @return
     */
    List<TodoReadDto> findByInstanceCode(@NotNull String instanceCode);


}
