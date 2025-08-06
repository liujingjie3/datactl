package com.zjlab.dataservice.modules.toolbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.toolbox.po.TaskPo;
import org.apache.ibatis.annotations.Param;

public interface ToolBoxTaskMapper extends BaseMapper<TaskPo> {
    int updateLog(@Param("taskId") String taskId, @Param("taskConfiguration") String taskConfiguration);
}
