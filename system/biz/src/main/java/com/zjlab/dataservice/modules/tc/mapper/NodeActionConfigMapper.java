package com.zjlab.dataservice.modules.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.tc.model.entity.NodeActionConfig;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 操作控制项配置 Mapper
 */
public interface NodeActionConfigMapper extends BaseMapper<NodeActionConfig> {
    /**
     * 批量查询控制项配置
     */
    List<NodeActionConfig> selectByIds(@Param("ids") List<Long> ids);
}
