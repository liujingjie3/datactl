package com.zjlab.dataservice.modules.tc.mapper;

import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.vo.TaskNodeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 节点实例相关 Mapper
 */
public interface TcTaskNodeInstMapper {
    int insertNodeInst(@Param("taskId") Long taskId,
                       @Param("templateId") String templateId,
                       @Param("nodeId") Long nodeId,
                       @Param("handlerRoleIds") String handlerRoleIds,
                       @Param("orderNo") Integer orderNo,
                       @Param("maxDuration") Integer maxDuration,
                       @Param("userId") String userId);

    int updateNodeInstPrevNext(@Param("nodeInstId") Long nodeInstId,
                               @Param("prevNodeIds") String prevNodeIds,
                               @Param("nextNodeIds") String nextNodeIds,
                               @Param("userId") String userId);

    int activateNodeInst(@Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    int completeNodeInst(@Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    List<CurrentNodeRow> selectCurrentNodes(@Param("taskIds") List<Long> taskIds);

    Long countDoneNodeInst(@Param("taskId") Long taskId);

    int updateNodeInstCancel(@Param("taskId") Long taskId, @Param("userId") String userId);

    List<Long> selectEndNodeInstIds(@Param("taskId") Long taskId);

    int insertViewNodeInst(@Param("taskId") Long taskId,
                           @Param("templateId") String templateId,
                           @Param("nodeId") Long nodeId,
                           @Param("prevNodeIds") String prevNodeIds,
                           @Param("handlerRoleIds") String handlerRoleIds,
                           @Param("userId") String userId);

    Long selectLastInsertId();

    int appendViewNodeToEndNodes(@Param("nodeInstId") Long nodeInstId,
                                 @Param("endInstIds") List<Long> endInstIds,
                                 @Param("userId") String userId);

    Long selectViewNodeInstId(@Param("taskId") Long taskId, @Param("nodeId") Long nodeId);

    int deleteNodeInst(@Param("nodeInstId") Long nodeInstId, @Param("userId") String userId);

    int clearEndNodeNext(@Param("taskId") Long taskId,
                         @Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    String selectNextNodeIds(@Param("nodeInstId") Long nodeInstId);

    int increaseArrivedCount(@Param("nodeInstId") Long nodeInstId, @Param("userId") String userId);

    String selectHandlerRoleIds(@Param("nodeInstId") Long nodeInstId);

    Long countOngoingNodeInst(@Param("taskId") Long taskId);

    int updateNodeInstAbort(@Param("taskId") Long taskId, @Param("userId") String userId);

    Integer selectNodeInstStatus(@Param("nodeInstId") Long nodeInstId);

    /** 按任务ID查询所有节点实例 */
    List<TaskNodeVO> selectNodeInstsByTaskId(@Param("taskId") Long taskId);
}

