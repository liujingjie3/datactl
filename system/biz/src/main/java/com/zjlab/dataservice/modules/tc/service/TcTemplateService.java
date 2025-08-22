package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.QueryListDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateQueryListVo;
import org.springframework.web.multipart.MultipartFile;

public interface TcTemplateService  extends IService<TodoTemplate> {

    /**
     * 自定义分页
     *
     */
    PageResult<TemplateQueryListVo> qryTemplateList(QueryListDto queryListDto);


    /**
     * 创建或者更新模板数据
     * @param templateVO
     * @return
     */
    TodoTemplateDto submit(MultipartFile file, TodoTemplateDto templateVO) throws Exception;

    TodoTemplateDto update(MultipartFile file, TodoTemplateDto templateVO) throws Exception;


    /**
     * 通过名称查询模板实例
     * @return
     */
    TodoTemplateDto findOne(TodoTemplateQueryDto todoTemplateQueryVO);

    /**
     * 通过模板编码批量删除
     * @param templateQueryVO
     * @return
     */
    TodoTemplateQueryDto deleteByCode(TodoTemplateQueryDto templateQueryVO);


    /**
     * 通过模板ID删除
     * @param templateQueryVO
     * @return
     */
    TodoTemplateQueryDto deleteByTemplateId(TodoTemplateQueryDto templateQueryVO);


    /**
     * 模板分页查询
     * @param basePage
     * @return
     */
    PageResult<TodoTemplateDto> findByUserIdPage(TodoTemplateQueryDto basePage);

}
