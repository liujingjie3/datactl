package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventFailDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEventFail;
import org.apache.ibatis.annotations.Param;


public interface TodoEventFailMapper extends BaseMapper<TodoEventFail> {
    /**
     * 分页查询失败事件
     */
    IPage<TodoEventFailDto> selectTodoEventFailPage(IPage<TodoEventFailDto> page, @Param("request") TodoEventFailDto request);
}
