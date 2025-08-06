package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.tc.model.dto.TodoInstanceDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTaskQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoInstance;
import com.zjlab.dataservice.modules.tc.model.po.TodoInstancePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TodoInstanceMapper extends BaseMapper<TodoInstance> {
    /**
     * 分页查询
     *
     * @return
     */
    IPage<TodoInstanceDto> selectTodoInstancePage(IPage<TodoInstanceDto> page, @Param("request") TodoInstanceDto request);

    /**
     * 查询是否存在一个实例对象
     * @param instance
     * @return
     */
    TodoInstance selectOneByInstance(@Param("request")TodoInstance instance);
}
