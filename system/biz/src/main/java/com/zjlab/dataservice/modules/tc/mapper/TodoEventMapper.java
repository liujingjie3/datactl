package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEvent;
import org.apache.ibatis.annotations.Param;

public interface TodoEventMapper extends BaseMapper<TodoEvent> {
    /**
     * 分页查询事件
     */
    IPage<TodoEventDto> selectTodoEventPage(IPage<TodoEventDto> page, @Param("request") TodoEventDto request);
}
