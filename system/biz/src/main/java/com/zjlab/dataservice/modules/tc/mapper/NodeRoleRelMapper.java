package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.entity.NodeRoleRel;
import org.apache.ibatis.annotations.Param;

/**
 * 节点角色关联 Mapper
 */
public interface NodeRoleRelMapper extends BaseMapper<NodeRoleRel> {
    /**
     * 根据节点ID查询角色关联
     */
    NodeRoleRel selectByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 根据节点ID删除角色关联
     */
    int deleteByNodeId(@Param("nodeId") Long nodeId);
}
