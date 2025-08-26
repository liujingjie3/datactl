package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.dto.NodeActionSubmitDto;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateNodeFlowVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskDetailVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;

import java.util.List;

/**
 * 任务相关服务
 */
public interface TcTaskManagerService {
    /**
     * 创建任务
     *
     * @param dto 创建参数
     * @return 新任务的ID
     */
    Long createTask(TaskManagerCreateDto dto);

    /**
     * 分页查询任务列表
     *
     * @param query 查询条件
     * @return 分页任务列表
     */
    PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     */
    void cancelTask(Long taskId);

    /**
     * 编辑任务
     *
     * @param dto 编辑参数
     */
    void editTask(TaskManagerEditDto dto);

    /**
     * 根据任务模板ID查询节点流
     *
     * @param templateId 模板ID
     * @return 节点流列表
     */
    List<TemplateNodeFlowVO> listNodeFlows(String templateId);

    /**
     * 提交节点操作
     *
     * @param dto 节点操作参数
     */
    void submitAction(NodeActionSubmitDto dto);

    /**
     * 查询任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDetailVO getTaskDetail(Long taskId);

    /**
     * 查询遥控指令单
     *
     * @param taskId 任务ID
     * @return 遥控指令单列表
     */
    List<RemoteCmdExportVO> getRemoteCmds(Long taskId);

    /**
     * 查询轨道计划
     *
     * @param taskId 任务ID
     * @return 轨道计划列表
     */
    List<OrbitPlanExportVO> getOrbitPlans(Long taskId);
}
