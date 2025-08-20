package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表相关查询
 */
public interface TcTaskManagerMapper {
    /** 查询列表 */
    List<TaskManagerListItemVO> selectTaskList(@Param("query") TaskManagerListQuery query);
    /** 统计总数 */
    Long countTaskList(@Param("query") TaskManagerListQuery query);
    /** 查询当前节点 */
    List<CurrentNodeRow> selectCurrentNodes(@Param("taskIds") List<Long> taskIds);
}
