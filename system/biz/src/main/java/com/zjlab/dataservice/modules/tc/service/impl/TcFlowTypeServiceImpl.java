package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.Page;
import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.mapper.FlowTypeMapper;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;
import com.zjlab.dataservice.modules.tc.model.entity.FlowType;
import com.zjlab.dataservice.modules.tc.service.TcFlowTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcFlowTypeServiceImpl extends ServiceImpl<FlowTypeMapper, FlowType> implements
        TcFlowTypeService {

    @Override
    public PageResult<FlowTypeDto> findAllTypeName() {

        List<FlowType> types = this.baseMapper.selectAllTypeName();
        List<FlowTypeDto> typeVOS = types.stream()
                .map(type -> BeanUtil.copyProperties(type, FlowTypeDto.class))
                .collect(Collectors.toList());

        PageResult<FlowTypeDto> result = new PageResult<>();
        result.setPageNo(1);
        result.setPageSize(10);
        result.setTotal(typeVOS.size());
        result.setData(typeVOS);
        return result;
    }

}
