package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
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

    /** 插入节点实例 */
    int insertNodeInst(@Param("taskId") Long taskId,
                       @Param("templateId") String templateId,
                       @Param("nodeId") Long nodeId,
                       @Param("handlerRoleIds") String handlerRoleIds,
                       @Param("orderNo") Integer orderNo,
                       @Param("maxDuration") Integer maxDuration,
                       @Param("userId") String userId);

    /** 更新节点实例前后驱 */
    int updateNodeInstPrevNext(@Param("nodeInstId") Long nodeInstId,
                               @Param("prevNodeIds") String prevNodeIds,
                               @Param("nextNodeIds") String nextNodeIds,
                               @Param("userId") String userId);

    /** 激活节点实例 */
    int activateNodeInst(@Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);
    /** 完成节点实例 */
    int completeNodeInst(@Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);
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

    /** 查询任务编辑信息 */
    TaskManagerEditInfo selectTaskForEdit(@Param("taskId") Long taskId);

    /** 更新任务基本信息 */
    int updateTask(@Param("dto") TaskManagerEditDto dto, @Param("userId") String userId);

    /** 查询末端节点实例ID */
    List<Long> selectEndNodeInstIds(@Param("taskId") Long taskId);

    /** 插入查看结果节点实例 */
    int insertViewNodeInst(@Param("taskId") Long taskId,
                           @Param("templateId") String templateId,
                           @Param("nodeId") Long nodeId,
                           @Param("prevNodeIds") String prevNodeIds,
                           @Param("handlerRoleIds") String handlerRoleIds,
                           @Param("userId") String userId);

    /** 查询最近插入ID */
    Long selectLastInsertId();

    /** 末端节点追加指向 */
    int appendViewNodeToEndNodes(@Param("nodeInstId") Long nodeInstId,
                                 @Param("endInstIds") List<Long> endInstIds,
                                 @Param("userId") String userId);

    /** 查询查看结果节点实例ID */
    Long selectViewNodeInstId(@Param("taskId") Long taskId, @Param("nodeId") Long nodeId);

    /** 删除节点实例 */
    int deleteNodeInst(@Param("nodeInstId") Long nodeInstId, @Param("userId") String userId);

    /** 清理末端节点指向 */
    int clearEndNodeNext(@Param("taskId") Long taskId,
                         @Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    /** 根据角色ID集合查询用户ID */
    List<String> selectUserIdsByRoleIds(@Param("roleIds") List<String> roleIds);

    /** 根据模板ID查询节点流 */
    List<TemplateNodeFlowVO> selectTemplateNodeFlows(@Param("templateId") String templateId);

    /** 查询节点实例的后继ID JSON */
    String selectNextNodeIds(@Param("nodeInstId") Long nodeInstId);

    /** 增加节点实例的到达前驱计数 */
    int increaseArrivedCount(@Param("nodeInstId") Long nodeInstId, @Param("userId") String userId);

    /** 查询节点实例的办理角色ID集合JSON */
    String selectHandlerRoleIds(@Param("nodeInstId") Long nodeInstId);

    /** 统计任务进行中节点数量 */
    Long countOngoingNodeInst(@Param("taskId") Long taskId);

    /** 更新任务状态为已完成 */
    int updateTaskComplete(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 更新任务状态为异常结束 */
    int updateTaskAbort(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 异常结束节点实例 */
    int updateNodeInstAbort(@Param("taskId") Long taskId, @Param("userId") String userId);

    /** 查询节点实例状态 */
    Integer selectNodeInstStatus(@Param("nodeInstId") Long nodeInstId);

}
