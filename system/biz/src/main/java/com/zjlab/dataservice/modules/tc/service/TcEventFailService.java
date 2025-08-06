package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventFailDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEventFail;


public interface TcEventFailService extends IService<TodoEventFail> {

    PageResult<TodoEventFailDto> selectTodoEventFailPage(TodoEventFailDto failEvent);
}
