package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.dto.QueryListDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

public interface TodoTemplateMapper extends BaseMapper<TodoTemplate> {
    /**
     * 自定义分页
     *
     * @return
     */
    IPage<TodoTemplate> selectTodoTemplatePage(IPage<TodoTemplate> page, @Param("request") QueryListDto queryListDto);


    /**
     * 条件查询一条模板记录
     * @param template
     * @return
     */
    TodoTemplate selectOneByTemplate(TodoTemplate template);

    int updateTodoTemplate(TodoTemplate todoTemplate);

}
