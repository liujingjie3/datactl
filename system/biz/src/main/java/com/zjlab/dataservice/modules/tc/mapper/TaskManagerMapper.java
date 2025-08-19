package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.po.TaskManagerListItemPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表相关查询
 */
public interface TaskManagerMapper {
    /** 查询列表 */
    List<TaskManagerListItemPO> selectTaskList(@Param("query") TaskManagerListQuery query);
    /** 统计总数 */
    Long countTaskList(@Param("query") TaskManagerListQuery query);
}
