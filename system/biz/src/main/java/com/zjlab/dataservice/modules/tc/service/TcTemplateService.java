package com.zjlab.dataservice.modules.tc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TcTemplateService  extends IService<TodoTemplate> {

    /**
     * 自定义分页
     *
     */
    PageResult<TodoTemplateDto> qryTemplateList(TodoTemplateQueryDto todoTemplateQueryDto);


    /**
     * 创建或者更新模板数据
     * @param templateVO
     * @return
     */
    TodoTemplateDto submit(MultipartFile file, TodoTemplateDto templateVO) throws Exception;

    TodoTemplateDto update(MultipartFile file, TodoTemplateDto templateVO);


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
