package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.tc.mapper.NodeInfoMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeRoleRelMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskNodeActionRecordMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskManagerMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskNodeInstMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskWorkItemMapper;
import com.zjlab.dataservice.modules.notify.service.NotifyService;
import com.zjlab.dataservice.modules.notify.model.enums.BizTypeEnum;
import com.zjlab.dataservice.modules.notify.model.enums.ChannelEnum;
import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.NodeActionSubmitDto;
import com.zjlab.dataservice.modules.tc.model.dto.NodeActionDto;
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
import com.zjlab.dataservice.modules.tc.model.vo.SatelliteGroupVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskCountVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerTabEnum;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerStatusEnum;
import com.zjlab.dataservice.modules.tc.enums.NodeActionTypeEnum;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.zjlab.dataservice.modules.storage.service.impl.MinioFileServiceImpl;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import com.zjlab.dataservice.modules.storage.model.dto.ObjectDownloadDto;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.ss.usermodel.Workbook;

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

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private MinioFileServiceImpl minioFileService;

    private static final String BUCKET_NAME = "dspp";
    private static final String TASK_FOLDER = "/TJEOS/WORKDIR/FILE/task";


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
        // 管理员或总体部成员才能创建任务
        boolean admin = sysUserService.isAdmin(userId);
        boolean overall = sysUserService.isOverall(userId);
        if (!admin && !overall) {
            throw new BaseException(ResultCode.TASKMANAGE_NO_PERMISSION);
        }
        // 2. 校验是否需要成像以及成像区域
        if (dto.getNeedImaging() != null) {
            if (dto.getNeedImaging() == 1 && dto.getImagingAreaId() == null) {
                throw new BaseException(ResultCode.TASKMANAGE_IMAGING_AREA_REQUIRED);
            }
            if (dto.getNeedImaging() == 0) {
                dto.setImagingAreaId(null);
            }
        }



        // 3. 新建任务记录
        String taskCode = UUID.randomUUID().toString().replace("-", "");
        String satellitesJson = dto.getSatellites() == null ? null : JSON.toJSONString(dto.getSatellites());
        String remoteCmdsJson = dto.getRemoteCmds() == null ? null : JSON.toJSONString(dto.getRemoteCmds());
        //todo 遥感指令这里需要根据测运控数据计算，怎么存，存什么等后面需要进一步沟通，暂时先mock一个json
        String orbitPlansJson = dto.getOrbitPlans() == null ? null : JSON.toJSONString(dto.getOrbitPlans());
        dto.setSatellitesJson(satellitesJson);
        dto.setRemoteCmdsJson(remoteCmdsJson);
        dto.setOrbitPlansJson(orbitPlansJson);
        tcTaskManagerMapper.insertTask(taskCode, dto, userId);
        Long taskId = tcTaskManagerMapper.selectLastInsertId();

        // 4. 解析模板并初始化所有节点实例
        String attr = tcTaskManagerMapper.selectTemplateAttr(dto.getTemplateId());
        List<TemplateNode> nodes = parseTemplateNodes(attr);
        if (nodes.isEmpty()) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        // 4.1 插入节点实例并记录ID映射
        Map<String, Long> map = new HashMap<>();
        for (TemplateNode n : nodes) {
            String rolesJson = JSON.toJSONString(n.getRoleIds());
            String actionsJson = JSON.toJSONString(n.getActions());
            taskNodeInstMapper.insertNodeInst(taskId, dto.getTemplateId(), n.getNodeId(), rolesJson, n.getOrderNo(), n.getMaxDuration(), actionsJson, userId);
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
                    Integer maxOrderNo = taskNodeInstMapper.selectMaxOrderNo(taskId);
                    Integer orderNo = maxOrderNo == null ? 1 : maxOrderNo + 1;
                    Integer maxDuration = viewNode.getExpectedDuration();
                    taskNodeInstMapper.insertViewNodeInst(taskId, dto.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, orderNo, maxDuration, viewNode.getActions(), userId);
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
        Set<String> allUserIds = new HashSet<>();
        for (TemplateNode n : nodes) {
            Long instId = map.get(n.getKey());
            boolean isStart = n.getPrev() == null || n.getPrev().isEmpty();
            if (isStart) {
                taskNodeInstMapper.activateNodeInst(instId, userId);
            }
            List<String> roleIds = n.getRoleIds();
            List<String> userIds = new ArrayList<>();
            if (roleIds != null && !roleIds.isEmpty()) {
                userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                int phaseStatus = isStart ? 1 : 0;
                for (String uid : userIds) {
                    taskWorkItemMapper.insertWorkItem(taskId, instId, uid, phaseStatus, userId);
                }
                allUserIds.addAll(userIds);
            }
            if (isStart) {
                scheduleNodeTimeout(instId, n.getMaxDuration(), userIds, userId);
            }
        }

        // 7. 通知工作流全体（排除发起人）
        List<String> recipients = new ArrayList<>(allUserIds);
        recipients.remove(userId);
        if (!recipients.isEmpty()) {
            JSONObject payload = new JSONObject();
            payload.put("taskId", taskId);
            payload.put("taskName", dto.getTaskName());
            int channelCode = ChannelEnum.DINGTALK.getCode();
            String dedupKey = BizTypeEnum.TASK_CREATED.getCode() + "_" + taskId + "_" + channelCode;
            notifyService.enqueue((byte) BizTypeEnum.TASK_CREATED.getCode(), taskId,
                    (byte) channelCode, payload, recipients,
                    dedupKey, LocalDateTime.now(), userId);
        }

        // 7.1 进站前提醒：对所有圈次定时通知
        List<String> orbitRecipients = new ArrayList<>(allUserIds);
        orbitRecipients.add(userId);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (dto.getOrbitPlans() != null && !orbitRecipients.isEmpty()) {
            for (OrbitPlanExportVO plan : dto.getOrbitPlans()) {
                try {
                    LocalDateTime inTime = LocalDateTime.parse(plan.getInTime(), df);
                    for (int m = 60; m >= 0; m -= 15) {
                        LocalDateTime run = inTime.minusMinutes(m);
                        if (run.isAfter(LocalDateTime.now())) {
                            JSONObject payload = new JSONObject();
                            payload.put("taskId", taskId);
                            payload.put("taskName", dto.getTaskName());
                            payload.put("orbitNo", plan.getOrbitNo());
                            payload.put("inTime", plan.getInTime());
                            payload.put("remainMin", m);
                            int channelCode = ChannelEnum.DINGTALK.getCode();
                            String dedup = BizTypeEnum.ORBIT_REMIND.getCode() + "_" + taskId + "_" +
                                    plan.getOrbitNo() + "_" + m + "_" + channelCode;
                            notifyService.enqueue((byte) BizTypeEnum.ORBIT_REMIND.getCode(), taskId,
                                    (byte) channelCode, payload, orbitRecipients,
                                    dedup, run, userId);
                        }
                    }
                } catch (Exception ignore) {
                    // ignore parse errors
                }
            }
        }

        // 8. 返回任务ID
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
        boolean admin = sysUserService.isAdmin(userId);
        if (!admin && !userId.equals(info.getCreateBy())) {
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
        boolean admin = sysUserService.isAdmin(userId);
        if (!admin && !userId.equals(info.getCreateBy())) {
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

        // 4. 更新任务基础字段
        if (StringUtils.isBlank(dto.getTaskName())) {
            throw new BaseException(ResultCode.PARA_ERROR);
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
                    Integer maxOrderNo = taskNodeInstMapper.selectMaxOrderNo(dto.getTaskId());
                    Integer orderNo = maxOrderNo == null ? 1 : maxOrderNo + 1;
                    Integer maxDuration = viewNode.getExpectedDuration();
                    taskNodeInstMapper.insertViewNodeInst(dto.getTaskId(), info.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, orderNo, maxDuration, viewNode.getActions(), userId);
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
     * @param resultDisplayNeeded 是否需要结果展示（0 或 1，必传）
     * @return 节点流列表
     */
    @Override
    public List<TemplateNodeFlowVO> listNodeFlows(String templateId, Integer resultDisplayNeeded) {
        // 1. 参数校验
        if (StringUtils.isBlank(templateId)) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        // 2. 解析模板并生成节点流信息
        String attr = tcTaskManagerMapper.selectTemplateAttr(templateId);
        List<TemplateNode> nodes = parseTemplateNodes(attr);
        List<TemplateNodeFlowVO> result = new ArrayList<>();
        for (TemplateNode n : nodes) {
            TemplateNodeFlowVO vo = new TemplateNodeFlowVO();
            vo.setNodeName(n.getNodeName());
            vo.setNodeDescription(n.getNodeDescription());
            List<String> roleIds = n.getRoleIds();
            List<String> handlerNames = new ArrayList<>();
            if (roleIds != null && !roleIds.isEmpty()) {
                List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                if (userIds != null && !userIds.isEmpty()) {
                    List<SysUser> users = sysUserService.listByIds(userIds);
                    handlerNames = users.stream().map(SysUser::getRealname).collect(Collectors.toList());
                }
            }
            vo.setHandlerRealName(handlerNames);
            vo.setMaxDuration(n.getMaxDuration());
            vo.setOrderNo(n.getOrderNo());
            result.add(vo);
        }
        // 3. 根据需求追加查看影像结果节点
        if (resultDisplayNeeded == 1) {
            NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
            if (viewNode != null) {
                List<NodeRoleRel> rels = nodeRoleRelMapper.selectByNodeId(viewNode.getId());
                List<String> roleIds = rels.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toList());
                List<String> handlerNames = new ArrayList<>();
                if (!roleIds.isEmpty()) {
                    List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                    if (userIds != null && !userIds.isEmpty()) {
                        List<SysUser> users = sysUserService.listByIds(userIds);
                        handlerNames = users.stream().map(SysUser::getRealname).collect(Collectors.toList());
                    }
                }
                TemplateNodeFlowVO vo = new TemplateNodeFlowVO();
                vo.setNodeName(viewNode.getName());
                vo.setNodeDescription(viewNode.getDescription());
                vo.setHandlerRealName(handlerNames);
                vo.setMaxDuration(viewNode.getExpectedDuration());
                int orderNo = result.stream()
                    .map(TemplateNodeFlowVO::getOrderNo)
                    .filter(o -> o != null)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
                vo.setOrderNo(orderNo);
                result.add(vo);
            }
        }
        result.sort(Comparator.comparing(TemplateNodeFlowVO::getOrderNo, Comparator.nullsLast(Integer::compareTo)));
        return result;
    }

    /**
     * 提交节点操作
     *
     * @param dto 节点操作参数
     */
    @Override
    @Transactional
    public void submitAction(NodeActionSubmitDto dto, MultipartFile[] files) {
        // 1. 参数校验
        if (dto == null || dto.getTaskId() == null || dto.getNodeInstId() == null
                || dto.getActions() == null || dto.getActions().isEmpty()) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        // files入参只能上传一个
        if (files != null && files.length > 1) {
            throw new BaseException(ResultCode.TASKMANAGE_UPLOAD_ONLY_ONE_FILE);
        }
        // 2. 获取当前用户
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        // 3. 权限校验：节点需在办理中，且当前用户必须为该节点的当前操作人
        Integer nodeStatus = taskNodeInstMapper.selectNodeInstStatus(dto.getNodeInstId());
        if (nodeStatus == null) {
            throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
        }
        if (nodeStatus != 1) {
            throw new BaseException(ResultCode.STATUS_INVALID);
        }
        Long cnt = taskWorkItemMapper.countActiveWorkItem(dto.getTaskId(), dto.getNodeInstId(), userId);
        if (cnt == null || cnt == 0) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }

        String folder = TASK_FOLDER + "/taskId_" + dto.getTaskId() + "/nodeInstId_" + dto.getNodeInstId();

        // 4. 记录本次操作
        for (TaskNodeActionVO action : dto.getActions()) {
            NodeActionTypeEnum type = NodeActionTypeEnum.fromCode(action.getActionType());
            if (type != null) {
                if (type == NodeActionTypeEnum.UPLOAD) {
                    List<Map<String, String>> attachments = new ArrayList<>();
                    if (files != null) {
                        for (MultipartFile file : files) {
                            try {
                                String objectName = minioFileService.uploadReturnObjectNameWithOriginal(file, BUCKET_NAME, folder);
                                String newFileName = objectName.substring(objectName.lastIndexOf('/') + 1);
                                Map<String, String> att = new HashMap<>();
                                att.put("filename", file.getOriginalFilename());
                                att.put("storedFilename", newFileName);
                                att.put("url", objectName);
                                attachments.add(att);
                            } catch (Exception e) {
                                throw new BaseException(ResultCode.INTERNAL_SERVER_ERROR);
                            }
                        }
                    }
                    JSONObject payload = StringUtils.isNotBlank(action.getActionPayload())
                            ? JSON.parseObject(action.getActionPayload()) : new JSONObject();
                    payload.put("attachments", attachments);
                    action.setActionPayload(payload.toJSONString());
                } else if (type == NodeActionTypeEnum.SELECT_ORBIT_PLAN) {
                    JSONObject payload = StringUtils.isNotBlank(action.getActionPayload())
                            ? JSON.parseObject(action.getActionPayload()) : new JSONObject();
                    JSONArray orbitPlans = payload.getJSONArray("orbit_plans");
                    JSONObject newPayload = new JSONObject();
                    newPayload.put("orbit_plans", orbitPlans);
                    action.setActionPayload(newPayload.toJSONString());
                } else if (type == NodeActionTypeEnum.MODIFY_REMOTE_CMD) {
                    JSONObject payload = StringUtils.isNotBlank(action.getActionPayload())
                            ? JSON.parseObject(action.getActionPayload()) : new JSONObject();
                    JSONArray cmdArray = payload.getJSONArray("remote_cmds");
                    List<RemoteCmdExportVO> list = cmdArray == null
                            ? Collections.emptyList()
                            : cmdArray.toJavaList(RemoteCmdExportVO.class);
                    Workbook workbook = ExcelExportUtil.exportExcel(
                            new ExportParams("遥控指令单", "遥控指令单"),
                            RemoteCmdExportVO.class, list);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        workbook.write(baos);
                        workbook.close();
                    } catch (Exception e) {
                        throw new BaseException(ResultCode.INTERNAL_SERVER_ERROR);
                    }
                    String fileName = UUID.randomUUID().toString().replace("-", "") + "_遥控指令单.xlsx";
                    String objectName = folder + "/" + fileName;
                    try {
                        MinioUtil.uploadFile(BUCKET_NAME, objectName,
                                new ByteArrayInputStream(baos.toByteArray()));
                    } catch (Exception e) {
                        throw new BaseException(ResultCode.INTERNAL_SERVER_ERROR);
                    }
                    List<Map<String, String>> attachments = new ArrayList<>();
                    Map<String, String> att = new HashMap<>();
                    att.put("filename", fileName);
                    att.put("storedFilename", fileName);
                    att.put("url", objectName);
                    attachments.add(att);
                    payload.put("attachments", attachments);
                    action.setActionPayload(payload.toJSONString());
                }
            }
            TaskNodeActionRecord record = new TaskNodeActionRecord();
            record.setTaskId(dto.getTaskId());
            record.setNodeInstId(dto.getNodeInstId());
            record.setActionType(action.getActionType());
            record.setActionPayload(action.getActionPayload());
            record.setCreateBy(userId);
            record.setUpdateBy(userId);
            actionRecordMapper.insert(record);
        }

        // 5. 处理决策操作
        TaskNodeActionVO decisionAction = dto.getActions().stream()
                .filter(a -> a.getActionType() != null && a.getActionType() == NodeActionTypeEnum.DECISION.getCode())
                .findFirst().orElse(null);
        if (decisionAction != null) {
            boolean processed = true;
            if (decisionAction.getActionPayload() != null) {
                try {
                    JSONObject obj = JSON.parseObject(decisionAction.getActionPayload());
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
                List<String> users = getAllUserIds(dto.getTaskId(), true);
                if (!users.isEmpty()) {
                    JSONObject payload = new JSONObject();
                    payload.put("taskId", dto.getTaskId());
                    int channelCode = ChannelEnum.DINGTALK.getCode();
                    String dedupKey = BizTypeEnum.TASK_ABNORMAL.getCode() + "_" + dto.getTaskId() + "_" + channelCode;
                    notifyService.enqueue((byte) BizTypeEnum.TASK_ABNORMAL.getCode(), dto.getTaskId(),
                            (byte) channelCode, payload, users,
                            dedupKey, LocalDateTime.now(), userId);
                }
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
                        // 为激活的节点更新待办工作项状态
                        taskWorkItemMapper.activateWorkItem(dto.getTaskId(), nextId, userId);
                        List<String> userIds = taskWorkItemMapper.selectAssigneeIds(dto.getTaskId(), nextId);
                        if (!userIds.isEmpty()) {
                            JSONObject payload = new JSONObject();
                            payload.put("taskId", dto.getTaskId());
                            payload.put("nodeInstId", nextId);
                            int channelCode = ChannelEnum.DINGTALK.getCode();
                            String dedupKey = BizTypeEnum.NODE_DONE.getCode() + "_" + dto.getTaskId() + "_" +
                                    nextId + "_" + channelCode;
                            notifyService.enqueue((byte) BizTypeEnum.NODE_DONE.getCode(), nextId,
                                    (byte) channelCode, payload, userIds,
                                    dedupKey, LocalDateTime.now(), userId);
                            Integer maxDur = taskNodeInstMapper.selectMaxDuration(nextId);
                            scheduleNodeTimeout(nextId, maxDur, userIds, userId);
                        }
                    }
                }
            }

            // 5.3 若所有节点均已完成，则结束任务
            Long ongoing = taskNodeInstMapper.countOngoingNodeInst(dto.getTaskId());
            if (ongoing != null && ongoing == 0) {
                tcTaskManagerMapper.updateTaskComplete(dto.getTaskId(), userId);
                List<String> users = getAllUserIds(dto.getTaskId(), true);
                if (!users.isEmpty()) {
                    JSONObject payload = new JSONObject();
                    payload.put("taskId", dto.getTaskId());
                    int channelCode = ChannelEnum.DINGTALK.getCode();
                    String dedupKey = BizTypeEnum.TASK_FINISHED.getCode() + "_" + dto.getTaskId() + "_" + channelCode;
                    notifyService.enqueue((byte) BizTypeEnum.TASK_FINISHED.getCode(), dto.getTaskId(),
                            (byte) channelCode, payload, users,
                            dedupKey, LocalDateTime.now(), userId);
                }
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
        if (StringUtils.isNotBlank(detail.getRemoteCmdsJson())) {
            detail.setRemoteCmds(JSON.parseArray(detail.getRemoteCmdsJson(), RemoteCmdExportVO.class));
        }
        if (StringUtils.isNotBlank(detail.getOrbitPlansJson())) {
            detail.setOrbitPlans(JSON.parseArray(detail.getOrbitPlansJson(), OrbitPlanExportVO.class));
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
        Map<Long, Map<Integer, NodeActionDto>> nodeActionMap = new HashMap<>();
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

            if (StringUtils.isNotBlank(inst.getActions())) {
                List<NodeActionDto> actList = JSON.parseArray(inst.getActions(), NodeActionDto.class);
                if (actList != null) {
                    Map<Integer, NodeActionDto> map = new HashMap<>();
                    for (NodeActionDto a : actList) {
                        if (a != null && a.getType() != null) {
                            map.put(a.getType(), a);
                        }
                    }
                    nodeActionMap.put(inst.getNodeInstId(), map);
                    TaskHistoryNodeVO hv = historyMap.get(inst.getNodeInstId());
                    if (hv != null) {
                        hv.setActionConfig(actList);
                    }
                }
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
            for (TaskNodeActionRecord r : records) {
                TaskNodeActionVO av = new TaskNodeActionVO();
                av.setNodeInstId(r.getNodeInstId());
                av.setActionType(r.getActionType());
                av.setActionPayload(r.getActionPayload());

                String operatorName = null;
                if (StringUtils.isNotBlank(r.getCreateBy())) {
                    SysUser user = sysUserService.getById(r.getCreateBy());
                    if (user != null) {
                        operatorName = user.getRealname();
                    }
                }
                Map<Integer, NodeActionDto> actCfg = nodeActionMap.get(r.getNodeInstId());
                NodeActionDto cfg = actCfg == null ? null : actCfg.get(r.getActionType());
                String actionName = cfg != null ? cfg.getName() : null;
                String detailDesc = "";
                try {
                    JSONObject payload = StringUtils.isNotBlank(r.getActionPayload())
                            ? JSON.parseObject(r.getActionPayload()) : new JSONObject();
                    JSONArray attArr = payload.getJSONArray("attachments");
                    NodeActionTypeEnum recordType = NodeActionTypeEnum.fromCode(r.getActionType());
                    if (recordType != null) {
                        switch (recordType) {
                            case UPLOAD: {
                                String fileName = null;
                                if (attArr != null && !attArr.isEmpty()) {
                                    JSONObject first = attArr.getJSONObject(0);
                                    fileName = first == null ? null : first.getString("filename");
                                }
                                if (StringUtils.isNotBlank(fileName)) {
                                    detailDesc = "上传了附件：" + fileName;
                                } else {
                                    detailDesc = "上传了附件";
                                }
                                break;
                            }
                            case SELECT_ORBIT_PLAN: {
                                detailDesc = "";
                                break;
                            }
                            case DECISION: {
                                String yesText = "是";
                                String noText = "否";
                                String placeholderDecision = payload.getString("decision");
                                if (cfg != null && StringUtils.isNotBlank(cfg.getConfig())) {
                                    JSONObject c = JSON.parseObject(cfg.getConfig());
                                    yesText = StringUtils.defaultIfBlank(c.getString("yesText"), yesText);
                                    noText = StringUtils.defaultIfBlank(c.getString("noText"), noText);
                                }
                                if (placeholderDecision != null) {
                                    String chose = placeholderDecision;
                                    if ("yes".equalsIgnoreCase(placeholderDecision)) {
                                        chose = yesText;
                                    } else if ("no".equalsIgnoreCase(placeholderDecision)) {
                                        chose = noText;
                                    }
                                    detailDesc = "选择了：" + chose;
                                }
                                break;
                            }
                            case TEXT: {
                                String text = payload.getString("text");
                                String placeholder = payload.getString("placeholder");
                                detailDesc = (placeholder == null ? "" : placeholder) + "：" + (text == null ? "" : text);
                                break;
                            }
                            case MODIFY_REMOTE_CMD: {
                                detailDesc = "";
                                break;
                            }
                            default:
                                break;
                        }
                    }
                } catch (Exception ignore) {
                    // ignore parse errors
                }
                if (StringUtils.isBlank(actionName)) {
                    NodeActionTypeEnum typeEnum = NodeActionTypeEnum.fromCode(r.getActionType());
                    actionName = typeEnum == null ? "" : typeEnum.getDesc();
                }
                StringBuilder log = new StringBuilder();
                if (StringUtils.isNotBlank(operatorName)) {
                    log.append(operatorName);
                }
                log.append("进行了").append(actionName).append("操作");
                if (StringUtils.isNotBlank(detailDesc)) {
                    log.append("，").append(detailDesc);
                }
                av.setOperateLog(log.toString());
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
    public void downloadAttachment(String url, HttpServletResponse response) {
        if (StringUtils.isBlank(url)) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        try {
            ObjectDownloadDto dto = new ObjectDownloadDto();
            dto.setBucketName(BUCKET_NAME);
            dto.setPath(url);
            minioFileService.download(dto, response);
        } catch (Exception e) {
            throw new BaseException(ResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 解析模板属性JSON，生成带有前驱/后继关系的节点列表
     */
    private List<TemplateNode> parseTemplateNodes(String attr) {
        if (StringUtils.isBlank(attr)) {
            return new ArrayList<>();
        }
        JSONObject obj = JSON.parseObject(attr);
        JSONArray nodeArr = obj.getJSONArray("nodes");
        JSONArray edgeArr = obj.getJSONArray("edges");

        Map<String, TemplateNode> map = new LinkedHashMap<>();
        if (nodeArr != null) {
            for (int i = 0; i < nodeArr.size(); i++) {
                JSONObject nodeObj = nodeArr.getJSONObject(i);
                String key = nodeObj.getString("id");
                JSONObject params = nodeObj.getJSONObject("properties");
                if (params != null) {
                    params = params.getJSONObject("nodeConfigParams");
                }
                if (params == null) {
                    continue;
                }
                TemplateNode node = new TemplateNode();
                node.setKey(key);
                node.setNodeId(params.getLong("id"));
                node.setNodeName(params.getString("name"));
                node.setNodeDescription(params.getString("description"));
                node.setMaxDuration(params.getInteger("expectedDuration"));
                List<String> roleIds = new ArrayList<>();
                JSONArray roles = params.getJSONArray("roles");
                if (roles != null) {
                    for (int j = 0; j < roles.size(); j++) {
                        JSONObject r = roles.getJSONObject(j);
                        roleIds.add(r.getString("roleId"));
                    }
                }
                node.setRoleIds(roleIds);
                List<NodeActionDto> actions = new ArrayList<>();
                JSONArray acts = params.getJSONArray("actions");
                if (acts != null) {
                    for (int j = 0; j < acts.size(); j++) {
                        JSONObject a = acts.getJSONObject(j);
                        NodeActionDto na = new NodeActionDto();
                        na.setType(a.getInteger("type"));
                        na.setName(a.getString("name"));
                        na.setConfig(a.getString("config"));
                        actions.add(na);
                    }
                }
                node.setActions(actions);
                node.setPrev(new ArrayList<>());
                node.setNext(new ArrayList<>());
                map.put(key, node);
            }
        }

        if (edgeArr != null) {
            for (int i = 0; i < edgeArr.size(); i++) {
                JSONObject edge = edgeArr.getJSONObject(i);
                String s = edge.getString("sourceNodeId");
                String t = edge.getString("targetNodeId");
                TemplateNode sn = map.get(s);
                TemplateNode tn = map.get(t);
                if (sn != null && tn != null) {
                    sn.getNext().add(t);
                    tn.getPrev().add(s);
                }
            }
        }

        // 用入度法（Kahn 算法）做拓扑排序；同时用 BFS 思路按层级处理节点，给每个节点分配 orderNo；
        //遍历所有节点，把前驱数量作为入度值存到 indegree
        Map<String, Integer> indegree = new HashMap<>();
        for (TemplateNode n : map.values()) {
            indegree.put(n.getKey(), n.getPrev().size());
        }
        //找出入度为 0 的节点（起点）
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> e : indegree.entrySet()) {
            if (e.getValue() == 0) {
                queue.offer(e.getKey());
            }
        }
        // orderNo 从 1 开始
        int level = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String k = queue.poll();
                TemplateNode node = map.get(k);
                if (node != null) {
                    node.setOrderNo(level);
                    //当前节点被“处理”了，它的每个后继节点的入度减 1。
                    //如果后继节点的入度变成 0，说明它的所有前驱都已处理完，就可以加入队列等待下一层。
                    for (String next : node.getNext()) {
                        indegree.put(next, indegree.get(next) - 1);
                        if (indegree.get(next) == 0) {
                            queue.offer(next);
                        }
                    }
                }
            }
            level++;
        }

        return new ArrayList<>(map.values());
    }

    /**
     * 获取任务中所有节点相关用户ID
     */
    private List<String> getAllUserIds(Long taskId, boolean includeCreator) {
        List<String> users = taskWorkItemMapper.selectAssigneeIdsByTaskId(taskId);
        if (users == null) {
            users = new ArrayList<>();
        }
        if (includeCreator) {
            TaskManagerEditInfo info = tcTaskManagerMapper.selectTaskForEdit(taskId);
            if (info != null && StringUtils.isNotBlank(info.getCreateBy())) {
                users.add(info.getCreateBy());
            }
        }
        return users;
    }

    private void scheduleNodeTimeout(Long nodeInstId, Integer maxDuration,
                                     List<String> userIds, String operator) {
        if (maxDuration == null || maxDuration <= 0 || userIds == null || userIds.isEmpty()) {
            return;
        }
        LocalDateTime runTime = LocalDateTime.now().plusMinutes(maxDuration);
        JSONObject payload = new JSONObject();
        payload.put("nodeInstId", nodeInstId);
        int channelCode = ChannelEnum.DINGTALK.getCode();
        String dedupKey = BizTypeEnum.NODE_TIMEOUT.getCode() + "_" + nodeInstId + "_" + channelCode;
        notifyService.enqueue((byte) BizTypeEnum.NODE_TIMEOUT.getCode(), nodeInstId,
                (byte) channelCode, payload, userIds,
                dedupKey, runTime, operator);
    }

}
