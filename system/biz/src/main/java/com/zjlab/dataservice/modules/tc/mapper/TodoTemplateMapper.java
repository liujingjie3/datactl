package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TodoTemplateMapper extends BaseMapper<TodoTemplate> {
    /**
     * 自定义分页
     *
     * @return
     */
    IPage<TodoTemplateDto> selectTodoTemplatePage(IPage<TodoTemplateQueryDto> page, @Param("request") TodoTemplateQueryDto request);


    /**
     * 条件查询一条模板记录
     * @param template
     * @return
     */
    TodoTemplate selectOneByTemplate(TodoTemplate template);
}
