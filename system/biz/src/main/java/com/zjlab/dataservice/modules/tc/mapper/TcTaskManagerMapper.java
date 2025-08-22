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

    /** 查询任务状态 */
    Integer selectTaskStatus(@Param("taskId") Long taskId);

    /** 已完成节点数量 */
    Long countDoneNodeInst(@Param("taskId") Long taskId);

    /** 更新任务为取消 */
    int updateTaskCancel(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 取消节点实例 */
    int updateNodeInstCancel(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 取消工作项 */
    int updateWorkItemCancel(@Param("taskId") Long taskId, @Param("userId") String userId);
}
