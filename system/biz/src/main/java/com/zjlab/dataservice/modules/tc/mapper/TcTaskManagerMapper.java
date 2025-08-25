package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditInfo;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateNodeFlowVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表相关查询
 */
public interface TcTaskManagerMapper {
    /** 插入任务 */
    int insertTask(@Param("taskCode") String taskCode,
                   @Param("dto") TaskManagerCreateDto dto,
                   @Param("userId") String userId);

    /** 查询模板属性 */
    String selectTemplateAttr(@Param("templateId") String templateId);

    /** 查询列表 */
    List<TaskManagerListItemVO> selectTaskList(@Param("query") TaskManagerListQuery query);
    /** 统计总数 */
    Long countTaskList(@Param("query") TaskManagerListQuery query);

    /** 查询任务状态 */
    Integer selectTaskStatus(@Param("taskId") Long taskId);

    /** 更新任务为取消 */
    int updateTaskCancel(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 查询任务编辑信息 */
    TaskManagerEditInfo selectTaskForEdit(@Param("taskId") Long taskId);

    /** 更新任务基本信息 */
    int updateTask(@Param("dto") TaskManagerEditDto dto, @Param("userId") String userId);

    /** 查询最近插入ID */
    Long selectLastInsertId();

    /** 根据角色ID集合查询用户ID */
    List<String> selectUserIdsByRoleIds(@Param("roleIds") List<String> roleIds);

    /** 根据模板ID查询节点流 */
    List<TemplateNodeFlowVO> selectTemplateNodeFlows(@Param("templateId") String templateId);

    /** 更新任务状态为已完成 */
    int updateTaskComplete(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 更新任务状态为异常结束 */
    int updateTaskAbort(@Param("taskId") Long taskId, @Param("userId") String userId);

}
