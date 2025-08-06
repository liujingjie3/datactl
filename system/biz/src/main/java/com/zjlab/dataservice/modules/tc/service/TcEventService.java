package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEvent;


public interface TcEventService extends IService<TodoEvent> {

    PageResult<TodoEventDto> selectTodoEventPage(TodoEventDto todoEvent);
}
