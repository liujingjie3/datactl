package com.zjlab.dataservice.modules.toolbox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.toolbox.dto.FunctionModuleDto;
import com.zjlab.dataservice.modules.toolbox.po.FunctionModulePo;
import com.zjlab.dataservice.modules.toolbox.vo.FunctionModuleVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FunctionModuleService extends IService<FunctionModulePo> {


    /**
     * 列出所有工具箱功能模块
     * @return
     */
    List<FunctionModuleVo> listAllFunctionModules();

    /**
     * 新增功能模块
     * @param functionModuleDto
     * @return
     */
    FunctionModuleVo addFunctionModule(FunctionModuleDto functionModuleDto);

}
