package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.entity.FlowType;

import java.util.List;

public interface FlowTypeMapper extends BaseMapper<FlowType> {
    /**
     * 返回类型名称和编码列表
     * @return
     */
    List<FlowType> selectAllTypeName();
}
