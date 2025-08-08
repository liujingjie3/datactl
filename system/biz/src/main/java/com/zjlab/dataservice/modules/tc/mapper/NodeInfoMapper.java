package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.entity.NodeInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 节点信息 Mapper
 */
public interface NodeInfoMapper extends BaseMapper<NodeInfo> {
    /**
     * 根据名称查询节点
     */
    NodeInfo selectByName(@Param("name") String name);
}
