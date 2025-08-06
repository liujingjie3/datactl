package com.zjlab.dataservice.modules.toolbox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.toolbox.dto.FlowTemplateDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.po.FlowTemplatePo;
import com.zjlab.dataservice.modules.toolbox.vo.FlowTemplateVo;
import com.zjlab.dataservice.modules.toolbox.vo.FunctionModuleVo;

import java.util.List;

public interface FlowTemplateService extends IService<FlowTemplatePo> {

    /**
     * 列出所有我的工作流模板
     * @return
     */
    PageResult<FlowTemplateVo> listMyTemplate(ToolBoxDto toolBoxDto);

    /**
     * 列出所有公共的工作流模板
     * @return
     */
    PageResult<FlowTemplateVo> listPublicTemplate(ToolBoxDto toolBoxDto);

    /**
     * 列出所有模板
     * @param toolBoxDto
     * @return
     */
    PageResult<FlowTemplateVo> listAllTemplate(ToolBoxDto toolBoxDto);

    /**
     * 包含常规创建和复制创建
     * @return
     */
    FlowTemplateVo createTemplate(FlowTemplateDto flowTemplateDto);

    /**
     * 编辑后更新模板
     * @return
     */
    FlowTemplateVo updateTemplateById(FlowTemplateDto flowTemplateDto);

    /**
     * 查询单个模板
     * @param flowTemplateDto
     * @return
     */
    FlowTemplateVo queryTemplateById(FlowTemplateDto flowTemplateDto);

    /**
     * 查询单个模板
     * @param templateId
     * @return
     */
    FlowTemplatePo queryTemplateById(String templateId);

    /**
     * 进入画布时展示所有可拖拽节点
     * @return
     */
    List<FunctionModuleVo> listFunctionNodes();


}
