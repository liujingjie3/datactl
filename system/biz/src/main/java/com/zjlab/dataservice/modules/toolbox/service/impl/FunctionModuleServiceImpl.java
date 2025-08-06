package com.zjlab.dataservice.modules.toolbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.toolbox.dto.FunctionModuleDto;
import com.zjlab.dataservice.modules.toolbox.mapper.FunctionModuleMapper;
import com.zjlab.dataservice.modules.toolbox.po.FunctionModulePo;
import com.zjlab.dataservice.modules.toolbox.service.FunctionModuleService;
import com.zjlab.dataservice.modules.toolbox.vo.FunctionModuleVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class FunctionModuleServiceImpl extends ServiceImpl<FunctionModuleMapper, FunctionModulePo> implements FunctionModuleService {

    private final String ADMIN = "admin";

    @Override
    public List<FunctionModuleVo> listAllFunctionModules() {
        List<FunctionModuleVo> result = new ArrayList<>();
        LambdaQueryWrapper<FunctionModulePo> query = new LambdaQueryWrapper<>();
        query.eq(FunctionModulePo::getUserId, ADMIN);
        List<FunctionModulePo> functionModulePoList = baseMapper.selectList(query);
        for (FunctionModulePo po: functionModulePoList) {
            FunctionModuleVo tmpVo = new FunctionModuleVo();
            BeanUtils.copyProperties(po, tmpVo);
            result.add(tmpVo);
        }
        return result;
    }

    @Override
    public FunctionModuleVo addFunctionModule(FunctionModuleDto functionModuleDto) {

        return null;
    }


}
