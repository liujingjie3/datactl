package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.TaskListQuery;
import com.zjlab.dataservice.modules.tc.model.po.TaskListItemPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表相关查询
 */
public interface TaskMapper {
    /** 查询列表 */
    List<TaskListItemPO> selectTaskList(@Param("query") TaskListQuery query);
    /** 统计总数 */
    Long countTaskList(@Param("query") TaskListQuery query);
}
