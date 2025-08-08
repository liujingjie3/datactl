package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.entity.NodeActionRel;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 节点与操作控制项关联 Mapper
 */
public interface NodeActionRelMapper extends BaseMapper<NodeActionRel> {
    /**
     * 根据节点ID查询关联的控制项
     */
    List<NodeActionRel> selectByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 根据节点ID删除关联记录
     */
    int deleteByNodeId(@Param("nodeId") Long nodeId);
}
