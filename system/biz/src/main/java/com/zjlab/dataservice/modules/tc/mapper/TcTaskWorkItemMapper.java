package com.zjlab.dataservice.modules.tc.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 任务工作项相关操作
 */
public interface TcTaskWorkItemMapper {
    /** 插入工作项 */
    int insertWorkItem(@Param("taskId") Long taskId,
                       @Param("nodeInstId") Long nodeInstId,
                       @Param("assigneeId") String assigneeId,
                       @Param("phaseStatus") int phaseStatus,
                       @Param("userId") String userId);

    /** 删除节点的工作项 */
    int deleteWorkItemsByNodeInst(@Param("nodeInstId") Long nodeInstId,
                                  @Param("userId") String userId);

    /** 取消任务的工作项 */
    int updateWorkItemCancel(@Param("taskId") Long taskId,
                             @Param("userId") String userId);

    /** 更新当前用户工作项为已处理 */
    int updateMyWorkItem(@Param("taskId") Long taskId,
                         @Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    /** 更新其他用户工作项为他人已完成失效 */
    int updateOtherWorkItem(@Param("taskId") Long taskId,
                             @Param("nodeInstId") Long nodeInstId,
                             @Param("userId") String userId);

    /** 异常结束任务工作项 */
    int updateWorkItemAbort(@Param("taskId") Long taskId,
                            @Param("userId") String userId);

    /** 统计用户在节点的待处理工作项数量 */
    Long countActiveWorkItem(@Param("taskId") Long taskId,
                             @Param("nodeInstId") Long nodeInstId,
                             @Param("assigneeId") String assigneeId);

    /** 激活节点时更新工作项状态 */
    int activateWorkItem(@Param("taskId") Long taskId,
                         @Param("nodeInstId") Long nodeInstId,
                         @Param("userId") String userId);

    /** 查询节点的处理人 */
    List<String> selectAssigneeIds(@Param("taskId") Long taskId,
                                   @Param("nodeInstId") Long nodeInstId);
}

