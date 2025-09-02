package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.tc.mapper.NodeInfoMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeRoleRelMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskNodeActionRecordMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskManagerMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskNodeInstMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskWorkItemMapper;
import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.NodeActionSubmitDto;
import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditInfo;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.dto.TaskStatusCountDto;
import com.zjlab.dataservice.modules.tc.model.dto.TemplateNode;
import com.zjlab.dataservice.modules.tc.model.entity.NodeInfo;
import com.zjlab.dataservice.modules.tc.model.entity.NodeRoleRel;
import com.zjlab.dataservice.modules.tc.model.entity.TaskNodeActionRecord;
import com.zjlab.dataservice.modules.tc.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskDetailVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskNodeActionVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskNodeVO; // internal DTO
import com.zjlab.dataservice.modules.tc.model.vo.TaskHistoryNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskWorkflowNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateNodeFlowVO;
import com.zjlab.dataservice.modules.tc.model.dto.TemplateNodeFlowRow;
import com.zjlab.dataservice.modules.tc.model.vo.SatelliteGroupVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskCountVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerTabEnum;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerStatusEnum;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务相关服务实现
 */
@Service
public class TcTaskManagerServiceImpl implements TcTaskManagerService {

    @Autowired
    private TcTaskManagerMapper tcTaskManagerMapper;

    @Autowired
    private TcTaskNodeInstMapper taskNodeInstMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    @Autowired
    private NodeRoleRelMapper nodeRoleRelMapper;

    @Autowired
    private TcTaskNodeActionRecordMapper actionRecordMapper;

    @Autowired
    private TcTaskWorkItemMapper taskWorkItemMapper;


    /**
     * 创建任务并初始化节点实例
     *
     * @param dto 创建参数
     * @return 生成的任务ID
     */
    @Override
    @Transactional
    public Long createTask(TaskManagerCreateDto dto) {
        // 1. 参数及当前用户校验
        if (dto == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        // 2. 校验是否需要成像以及成像区域
        if (dto.getNeedImaging() != null) {
            if (dto.getNeedImaging() == 1 && StringUtils.isBlank(dto.getImagingArea())) {
                throw new BaseException(ResultCode.TASKMANAGE_IMAGING_AREA_REQUIRED);
            }
            if (dto.getNeedImaging() == 0) {
                dto.setImagingArea(null);
            }
        }

        // 3. 新建任务记录
        String taskCode = UUID.randomUUID().toString().replace("-", "");
        String satellitesJson = dto.getSatellites() == null ? null : JSON.toJSONString(dto.getSatellites());
        String remoteCmdsJson = dto.getRemoteCmds() == null ? null : JSON.toJSONString(dto.getRemoteCmds());
        String orbitPlansJson = dto.getOrbitPlans() == null ? null : JSON.toJSONString(dto.getOrbitPlans());
        dto.setSatellitesJson(satellitesJson);
        dto.setRemoteCmdsJson(remoteCmdsJson);
        dto.setOrbitPlansJson(orbitPlansJson);
        tcTaskManagerMapper.insertTask(taskCode, dto, userId);
        Long taskId = tcTaskManagerMapper.selectLastInsertId();

        // 4. 解析模板并初始化所有节点实例
        //todo 这里template_attr的结构还要再讨论，是否还需要中间表格也还要考虑下
        String attr = tcTaskManagerMapper.selectTemplateAttr(dto.getTemplateId());
        List<TemplateNode> nodes = new ArrayList<>();
        if (StringUtils.isNotBlank(attr)) {
            nodes = JSON.parseArray(attr, TemplateNode.class);
        }
        if (nodes.isEmpty()) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        // 4.1 插入节点实例并记录ID映射
        Map<String, Long> map = new HashMap<>();
        for (TemplateNode n : nodes) {
            String rolesJson = JSON.toJSONString(n.getRoleIds());
            taskNodeInstMapper.insertNodeInst(taskId, dto.getTemplateId(), n.getNodeId(), rolesJson, n.getOrderNo(), n.getMaxDuration(), userId);
            Long instId = taskNodeInstMapper.selectLastInsertId();
            map.put(n.getKey(), instId);
        }
        // 4.2 更新前驱、后继关系
        for (TemplateNode n : nodes) {
            Long instId = map.get(n.getKey());
            List<Long> prevIds = n.getPrev() == null ? Collections.emptyList() : n.getPrev().stream().map(map::get).collect(Collectors.toList());
            List<Long> nextIds = n.getNext() == null ? Collections.emptyList() : n.getNext().stream().map(map::get).collect(Collectors.toList());
            taskNodeInstMapper.updateNodeInstPrevNext(instId, JSON.toJSONString(prevIds), JSON.toJSONString(nextIds), userId);
        }

        // 5. 根据需求追加查看影像结果节点
        if (dto.getResultDisplayNeeded() != null && dto.getResultDisplayNeeded() == 1) {
            List<Long> endIds = taskNodeInstMapper.selectEndNodeInstIds(taskId);
            if (!endIds.isEmpty()) {
                NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
                if (viewNode != null) {
                    List<NodeRoleRel> rels = nodeRoleRelMapper.selectByNodeId(viewNode.getId());
                    List<String> roleIds = rels.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toList());
                    String prevNodeIds = JSON.toJSONString(endIds);
                    String handlerRoleIds = JSON.toJSONString(roleIds);
                    taskNodeInstMapper.insertViewNodeInst(taskId, dto.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, userId);
                    Long viewInstId = taskNodeInstMapper.selectLastInsertId();
                    if (viewInstId != null) {
                        taskNodeInstMapper.appendViewNodeToEndNodes(viewInstId, endIds, userId);
                        List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                        for (String uid : userIds) {
                            taskWorkItemMapper.insertWorkItem(taskId, viewInstId, uid, 0, userId);
                        }
                    }
                }
            }
        }

        // 6. 激活起始节点并生成工作项
        for (TemplateNode n : nodes) {
            Long instId = map.get(n.getKey());
            boolean isStart = n.getPrev() == null || n.getPrev().isEmpty();
            if (isStart) {
                taskNodeInstMapper.activateNodeInst(instId, userId);
            }
            List<String> roleIds = n.getRoleIds();
            if (roleIds != null && !roleIds.isEmpty()) {
                List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                int phaseStatus = isStart ? 1 : 0;
                for (String uid : userIds) {
                    taskWorkItemMapper.insertWorkItem(taskId, instId, uid, phaseStatus, userId);
                }
            }
        }

        // 7. 返回任务ID
        return taskId;
    }

    /**
     * 分页查询当前用户相关的任务
     *
     * @param query 查询条件
     * @return 任务分页结果
     */
    @Override
    public PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query) {
        // 1. 获取当前用户并验证
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        query.setUserId(userId);

        // 2. 根据用户角色判断可访问的标签
        boolean isAdmin = sysUserService.isAdmin(userId);
        TaskManagerTabEnum tab = TaskManagerTabEnum.fromValue(query.getTab());
        if (isAdmin) {
            if (tab != TaskManagerTabEnum.ALL) {
                throw new BaseException(ResultCode.TASKMANAGE_ADMIN_ONLY_ALL);
            }
        } else if (tab == TaskManagerTabEnum.ALL) {
            throw new BaseException(ResultCode.TASKMANAGE_ONLY_ADMIN_ALL);
        }

        // 3. 处理分页参数
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOffset((query.getPage() - 1) * query.getPageSize());

        // 4. 查询任务列表
        List<TaskManagerListItemVO> vos = tcTaskManagerMapper.selectTaskList(query);
        if (!vos.isEmpty()) {
            for (TaskManagerListItemVO vo : vos) {
                if (StringUtils.isNotBlank(vo.getSatellitesJson())) {
                    vo.setSatellites(JSON.parseArray(vo.getSatellitesJson(), SatelliteGroupVO.class));
                }
            }
            // 4.1 查询每个任务的当前节点及其角色信息
            List<Long> taskIds = vos.stream().map(TaskManagerListItemVO::getTaskId).collect(Collectors.toList());
            List<CurrentNodeRow> rows = taskNodeInstMapper.selectCurrentNodes(taskIds);
            Map<Long, Map<Long, CurrentNodeVO>> taskNodeMap = new HashMap<>();
            Set<String> roleIds = new HashSet<>();
            for (CurrentNodeRow row : rows) {
                Map<Long, CurrentNodeVO> nodeMap = taskNodeMap.computeIfAbsent(row.getTaskId(), k -> new LinkedHashMap<>());
                CurrentNodeVO node = nodeMap.computeIfAbsent(row.getNodeInstId(), k -> {
                    CurrentNodeVO cn = new CurrentNodeVO();
                    cn.setNodeInstId(row.getNodeInstId());
                    cn.setNodeName(row.getNodeName());
                    cn.setRoles(new ArrayList<>());
                    return cn;
                });
                if (StringUtils.isNotBlank(row.getHandlerRoleIds())) {
                    List<String> ids = JSON.parseArray(row.getHandlerRoleIds(), String.class);
                    if (ids != null) {
                        for (String rid : ids) {
                            NodeRoleDto role = new NodeRoleDto();
                            role.setRoleId(rid);
                            node.getRoles().add(role);
                            roleIds.add(rid);
                        }
                    }
                }
            }
            // 4.2 填充角色名称
            if (!roleIds.isEmpty()) {
                List<SysRole> roleList = sysRoleMapper.selectBatchIds(roleIds);
                Map<String, String> roleNameMap = roleList.stream()
                        .collect(Collectors.toMap(SysRole::getId, SysRole::getRoleName));
                for (Map<Long, CurrentNodeVO> nodeMap : taskNodeMap.values()) {
                    for (CurrentNodeVO node : nodeMap.values()) {
                        for (NodeRoleDto role : node.getRoles()) {
                            String name = roleNameMap.get(role.getRoleId());
                            if (name != null) {
                                role.setRoleName(name);
                            }
                        }
                    }
                }
            }
            // 4.3 将节点信息附加到结果中
            for (TaskManagerListItemVO vo : vos) {
                Map<Long, CurrentNodeVO> nodeMap = taskNodeMap.get(vo.getTaskId());
                if (nodeMap != null) {
                    vo.setCurrentNodes(new ArrayList<>(nodeMap.values()));
                } else {
                    vo.setCurrentNodes(new ArrayList<>());
                }
            }
        }
        // 5. 返回分页结果
        long total = tcTaskManagerMapper.countTaskList(query);
        return new PageResult<>(query.getPage(), query.getPageSize(), (int) total, vos);
    }

    /**
     * 取消任务及其工作项
     *
     * @param taskId 任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long taskId) {
        // 1. 参数校验并加载任务信息
        if (taskId == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        TaskManagerEditInfo info = tcTaskManagerMapper.selectTaskForEdit(taskId);
        if (info == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }

        // 2. 获取当前用户并校验权限
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        boolean isAdmin = sysUserService.isAdmin(userId);
        if (!isAdmin && !userId.equals(info.getCreateBy())) {
            throw new BaseException(ResultCode.TASKMANAGE_NO_PERMISSION);
        }

        // 3. 状态及完成度校验
        if (info.getStatus() == null || info.getStatus() != 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }
        Long doneCnt = taskNodeInstMapper.countDoneNodeInst(taskId);
        if (doneCnt != null && doneCnt > 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }

        // 4. 更新任务、节点实例和工作项状态为取消
        tcTaskManagerMapper.updateTaskCancel(taskId, userId);
        taskNodeInstMapper.updateNodeInstCancel(taskId, userId);
        taskWorkItemMapper.updateWorkItemCancel(taskId, userId);
    }

    /**
     * 编辑任务基本信息
     *
     * @param dto 编辑参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editTask(TaskManagerEditDto dto) {
        // 1. 参数校验并获取任务信息
        if (dto == null || dto.getTaskId() == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        TaskManagerEditInfo info = tcTaskManagerMapper.selectTaskForEdit(dto.getTaskId());
        if (info == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }

        // 2. 获取当前用户并校验权限
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        boolean isAdmin = sysUserService.isAdmin(userId);
        if (!isAdmin && !userId.equals(info.getCreateBy())) {
            throw new BaseException(ResultCode.TASKMANAGE_NO_PERMISSION);
        }

        // 3. 校验任务状态与节点完成情况
        if (info.getStatus() == null || info.getStatus() != 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_EDIT);
        }
        Long doneCnt = taskNodeInstMapper.countDoneNodeInst(dto.getTaskId());
        if (doneCnt != null && doneCnt > 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_EDIT);
        }

        // 4. 校验成像信息并更新任务基础字段
        if (dto.getNeedImaging() != null) {
            if (dto.getNeedImaging() == 1 && StringUtils.isBlank(dto.getImagingArea())) {
                throw new BaseException(ResultCode.TASKMANAGE_IMAGING_AREA_REQUIRED);
            }
            if (dto.getNeedImaging() == 0) {
                dto.setImagingArea(null);
            }
        }
        tcTaskManagerMapper.updateTask(dto, userId);

        // 5. 处理查看影像结果节点的增删
        Integer oldDisplay = info.getResultDisplayNeeded();
        Integer newDisplay = dto.getResultDisplayNeeded();
        if (oldDisplay == null) {
            oldDisplay = 0;
        }
        if (newDisplay == null) {
            newDisplay = 0;
        }

        if (oldDisplay == 0 && newDisplay == 1) {
            // 5.1 原无展示节点，新配置需要展示时新增节点
            List<Long> endIds = taskNodeInstMapper.selectEndNodeInstIds(dto.getTaskId());
            if (!endIds.isEmpty()) {
                NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
                if (viewNode != null) {
                    List<NodeRoleRel> rels = nodeRoleRelMapper.selectByNodeId(viewNode.getId());
                    List<String> roleIds = rels.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toList());
                    String prevNodeIds = JSON.toJSONString(endIds);
                    String handlerRoleIds = JSON.toJSONString(roleIds);
                    taskNodeInstMapper.insertViewNodeInst(dto.getTaskId(), info.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, userId);
                    Long viewInstId = taskNodeInstMapper.selectLastInsertId();
                    if (viewInstId != null) {
                        taskNodeInstMapper.appendViewNodeToEndNodes(viewInstId, endIds, userId);
                        List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                        for (String uid : userIds) {
                            taskWorkItemMapper.insertWorkItem(dto.getTaskId(), viewInstId, uid, 0, userId);
                        }
                    }
                }
            }
        } else if (oldDisplay == 1 && newDisplay == 0) {
            // 5.2 原配置展示节点，新配置不需要时删除节点
            NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
            if (viewNode != null) {
                Long viewInstId = taskNodeInstMapper.selectViewNodeInstId(dto.getTaskId(), viewNode.getId());
                if (viewInstId != null) {
                    taskNodeInstMapper.deleteNodeInst(viewInstId, userId);
                    taskNodeInstMapper.clearEndNodeNext(dto.getTaskId(), viewInstId, userId);
                    taskWorkItemMapper.deleteWorkItemsByNodeInst(viewInstId, userId);
                }
            }
        }
    }

    /**
     * 根据任务模板ID查询节点流
     *
     * @param templateId 模板ID
     * @return 节点流列表
     */
    @Override
    public List<TemplateNodeFlowVO> listNodeFlows(String templateId) {
        // 1. 参数校验
        if (StringUtils.isBlank(templateId)) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        // 2. 查询并返回节点流信息
        // todo 这里的最大时长是从tc_template_node表里拿的，后续看模板任务怎么设计再改
        List<TemplateNodeFlowRow> rows = tcTaskManagerMapper.selectTemplateNodeFlows(templateId);
        List<TemplateNodeFlowVO> result = new ArrayList<>();
        for (TemplateNodeFlowRow row : rows) {
            TemplateNodeFlowVO vo = new TemplateNodeFlowVO();
            vo.setNodeName(row.getNodeName());
            vo.setNodeDescription(row.getNodeDescription());
            if (StringUtils.isNotBlank(row.getHandlerRealName())) {
                vo.setHandlerRealName(Arrays.asList(row.getHandlerRealName().split(",")));
            } else {
                vo.setHandlerRealName(new ArrayList<>());
            }
            vo.setMaxDuration(row.getMaxDuration());
            vo.setOrderNo(row.getOrderNo());
            result.add(vo);
        }
        return result;
    }

    /**
     * 提交节点操作
     *
     * @param dto 节点操作参数
     */
    @Override
    @Transactional
    public void submitAction(NodeActionSubmitDto dto) {
        // 1. 参数校验
        if (dto == null || dto.getTaskId() == null || dto.getNodeInstId() == null || dto.getActionType() == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        // 2. 获取当前用户
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }

        // 3. 权限校验：节点需在办理中，且当前用户必须属于处理角色
        Integer nodeStatus = taskNodeInstMapper.selectNodeInstStatus(dto.getNodeInstId());
        if (nodeStatus == null) {
            throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
        }
        if (nodeStatus != 1) {
            throw new BaseException(ResultCode.STATUS_INVALID);
        }
        String roleJson = taskNodeInstMapper.selectHandlerRoleIds(dto.getNodeInstId());
        List<String> roleIds = JSON.parseArray(roleJson, String.class);
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }
        List<String> permitted = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
        if (permitted == null || !permitted.contains(userId)) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }

        // 4. 记录本次操作
        TaskNodeActionRecord record = new TaskNodeActionRecord();
        record.setTaskId(dto.getTaskId());
        record.setNodeInstId(dto.getNodeInstId());
        record.setActionType(dto.getActionType());
        record.setActionPayload(dto.getActionPayload());
        record.setCreateBy(userId);
        record.setUpdateBy(userId);
        actionRecordMapper.insert(record);

        // 5. 处理决策操作
        if (dto.getActionType() == 2) {
            boolean processed = true;
            if (dto.getActionPayload() != null) {
                try {
                    JSONObject obj = JSON.parseObject(dto.getActionPayload());
                    String decision = obj.getString("decision");
                    if (decision != null && !"yes".equalsIgnoreCase(decision)) {
                        processed = false;
                    }
                } catch (Exception ignore) {
                    // ignore parse errors and treat as processed
                }
            }
            // 5.1 决策拒绝，终止任务
            if (!processed) {
                tcTaskManagerMapper.updateTaskAbort(dto.getTaskId(), userId);
                taskNodeInstMapper.updateNodeInstAbort(dto.getTaskId(), userId);
                taskWorkItemMapper.updateWorkItemAbort(dto.getTaskId(), userId);
                return;
            }

            // 5.2 决策通过，完成当前节点并推进后继节点
            taskNodeInstMapper.completeNodeInst(dto.getNodeInstId(), userId);
            taskWorkItemMapper.updateMyWorkItem(dto.getTaskId(), dto.getNodeInstId(), userId);
            taskWorkItemMapper.updateOtherWorkItem(dto.getTaskId(), dto.getNodeInstId(), userId);

            String nextJson = taskNodeInstMapper.selectNextNodeIds(dto.getNodeInstId());
            List<Long> nextIds = JSON.parseArray(nextJson, Long.class);
            if (nextIds != null) {
                for (Long nextId : nextIds) {
                    // 增加到达次数并尝试激活后继节点
                    taskNodeInstMapper.increaseArrivedCount(nextId, userId);
                    int activated = taskNodeInstMapper.activateNodeInst(nextId, userId);
                    if (activated > 0) {
                        // 为激活的节点创建待办工作项
                        String nextRolesJson = taskNodeInstMapper.selectHandlerRoleIds(nextId);
                        List<String> nextRoleIds = JSON.parseArray(nextRolesJson, String.class);
                        if (nextRoleIds != null && !nextRoleIds.isEmpty()) {
                            List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(nextRoleIds);
                            for (String uid : userIds) {
                                taskWorkItemMapper.insertWorkItem(dto.getTaskId(), nextId, uid, 1, userId);
                            }
                        }
                    }
                }
            }

            // 5.3 若所有节点均已完成，则结束任务
            Long ongoing = taskNodeInstMapper.countOngoingNodeInst(dto.getTaskId());
            if (ongoing != null && ongoing == 0) {
                tcTaskManagerMapper.updateTaskComplete(dto.getTaskId(), userId);
            }
        }
    }

    @Override
    public TaskDetailVO getTaskDetail(Long taskId) {
        // 1. 参数校验并查询任务基本信息
        if (taskId == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        TaskDetailVO detail = tcTaskManagerMapper.selectTaskDetail(taskId);
        if (detail == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }
        if (StringUtils.isNotBlank(detail.getSatellitesJson())) {
            detail.setSatellites(JSON.parseArray(detail.getSatellitesJson(), SatelliteGroupVO.class));
        }

        // 3. 查询当前节点信息
        List<CurrentNodeRow> rows = taskNodeInstMapper.selectCurrentNodes(Collections.singletonList(taskId));
        List<CurrentNodeVO> currentNodes = new ArrayList<>();
        for (CurrentNodeRow row : rows) {
            CurrentNodeVO cn = new CurrentNodeVO();
            cn.setNodeInstId(row.getNodeInstId());
            cn.setNodeName(row.getNodeName());
            currentNodes.add(cn);
        }
        detail.setCurrentNodes(currentNodes);

        // 4. 构建工作流概况和历史记录
        List<TaskNodeVO> insts = taskNodeInstMapper.selectNodeInstsByTaskId(taskId);
        List<TaskWorkflowNodeVO> overview = new ArrayList<>();
        List<TaskHistoryNodeVO> history = new ArrayList<>();
        Map<Long, TaskHistoryNodeVO> historyMap = new HashMap<>();
        Set<String> roleIds = new HashSet<>();
        for (TaskNodeVO inst : insts) {
            // 概况信息
            TaskWorkflowNodeVO ov = new TaskWorkflowNodeVO();
            ov.setNodeInstId(inst.getNodeInstId());
            ov.setNodeName(inst.getNodeName());
            ov.setOrderNo(inst.getOrderNo());
            ov.setStatus(inst.getStatus());
            if (StringUtils.isNotBlank(inst.getPrevNodeIdsJson())) {
                ov.setPrevNodeIds(JSON.parseArray(inst.getPrevNodeIdsJson(), Long.class));
            } else {
                ov.setPrevNodeIds(new ArrayList<>());
            }
            if (StringUtils.isNotBlank(inst.getNextNodeIdsJson())) {
                ov.setNextNodeIds(JSON.parseArray(inst.getNextNodeIdsJson(), Long.class));
            } else {
                ov.setNextNodeIds(new ArrayList<>());
            }
            overview.add(ov);

            // 历史信息，包含已办理和当前节点
            if (inst.getStatus() != null && inst.getStatus() != 0) {
                TaskHistoryNodeVO hv = new TaskHistoryNodeVO();
                hv.setNodeInstId(inst.getNodeInstId());
                hv.setNodeName(inst.getNodeName());
                hv.setOrderNo(inst.getOrderNo());
                hv.setStatus(inst.getStatus());
                if (inst.getStatus() == 2) {
                    hv.setProcessTime(inst.getCompletedAt());
                    if (StringUtils.isNotBlank(inst.getCompletedBy())) {
                        SysUser user = sysUserService.getById(inst.getCompletedBy());
                        if (user != null) {
                            hv.setOperatorName(user.getRealname());
                        }
                    }
                } else {
                    hv.setRoles(new ArrayList<>());
                    if (StringUtils.isNotBlank(inst.getHandlerRoleIds())) {
                        List<String> ids = JSON.parseArray(inst.getHandlerRoleIds(), String.class);
                        if (ids != null) {
                            for (String rid : ids) {
                                NodeRoleDto role = new NodeRoleDto();
                                role.setRoleId(rid);
                                hv.getRoles().add(role);
                                roleIds.add(rid);
                            }
                        }
                    }
                }
                hv.setActionLogs(new ArrayList<>());
                history.add(hv);
                historyMap.put(inst.getNodeInstId(), hv);
            }
        }

        if (!roleIds.isEmpty()) {
            List<SysRole> roleList = sysRoleMapper.selectBatchIds(roleIds);
            Map<String, String> roleNameMap = roleList.stream()
                    .collect(Collectors.toMap(SysRole::getId, SysRole::getRoleName));
            for (TaskHistoryNodeVO hv : history) {
                if (hv.getRoles() != null) {
                    for (NodeRoleDto r : hv.getRoles()) {
                        String name = roleNameMap.get(r.getRoleId());
                        if (name != null) {
                            r.setRoleName(name);
                        }
                    }
                }
            }
        }

        // 5. 拼装节点操作日志
        List<TaskNodeActionRecord> records = actionRecordMapper.selectList(
                new QueryWrapper<TaskNodeActionRecord>().eq("task_id", taskId).orderByAsc("create_time"));
        if (!records.isEmpty()) {
            Set<String> userIds = records.stream().map(TaskNodeActionRecord::getCreateBy)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            Map<String, String> userNameMap = new HashMap<>();
            if (!userIds.isEmpty()) {
                List<SysUser> users = sysUserService.listByIds(userIds);
                if (users != null) {
                    for (SysUser u : users) {
                        userNameMap.put(u.getId(), u.getRealname());
                    }
                }
            }
            for (TaskNodeActionRecord r : records) {
                // 组装单条操作日志
                TaskNodeActionVO av = new TaskNodeActionVO();
                av.setNodeInstId(r.getNodeInstId());
                av.setActionType(r.getActionType());
                av.setActionPayload(r.getActionPayload());
                av.setActionTime(r.getCreateTime());
                av.setOperatorId(r.getCreateBy());
                if (StringUtils.isNotBlank(r.getCreateBy())) {
                    av.setOperatorName(userNameMap.get(r.getCreateBy()));
                }
                TaskHistoryNodeVO hv = historyMap.get(r.getNodeInstId());
                if (hv != null) {
                    hv.getActionLogs().add(av);
                }
            }
        }

        // 6. 设置返回值
        detail.setHistory(history);
        detail.setWorkflow(overview);
        return detail;
    }

    @Override
    public List<RemoteCmdExportVO> getRemoteCmds(Long taskId) {
        if (taskId == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        String json = tcTaskManagerMapper.selectRemoteCmds(taskId);
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(json, RemoteCmdExportVO.class);
    }

    @Override
    public List<OrbitPlanExportVO> getOrbitPlans(Long taskId) {
        if (taskId == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        String json = tcTaskManagerMapper.selectOrbitPlans(taskId);
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(json, OrbitPlanExportVO.class);
    }

    @Override
    public List<OrbitPlanExportVO> getRealtimeOrbitPlans() {
        // TODO 调用测运控接口实时计算轨道计划
        return Collections.emptyList();
    }

    @Override
    public TaskCountVO countTasks() {
        List<TaskStatusCountDto> list = tcTaskManagerMapper.countTaskByStatus();
        long running = 0L;
        long completed = 0L;
        long canceled = 0L;
        if (list != null) {
            for (TaskStatusCountDto dto : list) {
                if (dto == null || dto.getStatus() == null) {
                    continue;
                }
                if (dto.getStatus() == TaskManagerStatusEnum.RUNNING.getCode()) {
                    running += dto.getCount();
                } else if (dto.getStatus() == TaskManagerStatusEnum.CANCELED.getCode()) {
                    canceled += dto.getCount();
                } else if (dto.getStatus() == TaskManagerStatusEnum.FINISHED.getCode()
                        || dto.getStatus() == TaskManagerStatusEnum.ABNORMAL_END.getCode()) {
                    completed += dto.getCount();
                }
            }
        }
        long total = running + completed + canceled;
        TaskCountVO vo = new TaskCountVO();
        vo.setTotal(total);
        vo.setRunning(running);
        vo.setCompleted(completed);
        vo.setCanceled(canceled);
        return vo;
    }

    @Override
    public String getCurrentUserRealName() {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            return "";
        }
        SysUser user = sysUserService.getById(userId);
        return user != null ? user.getRealname() : "";
    }
}
