package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.modules.tc.mapper.NodeInfoMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeRoleRelMapper;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskManagerMapper;
import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditInfo;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.dto.TemplateNode;
import com.zjlab.dataservice.modules.tc.model.entity.NodeInfo;
import com.zjlab.dataservice.modules.tc.model.entity.NodeRoleRel;
import com.zjlab.dataservice.modules.tc.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerTabEnum;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    @Autowired
    private NodeRoleRelMapper nodeRoleRelMapper;

    @Override
    @Transactional
    public Long createTask(TaskManagerCreateDto dto) {
        if (dto == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        if (dto.getNeedImaging() != null) {
            if (dto.getNeedImaging() == 1 && StringUtils.isBlank(dto.getImagingArea())) {
                throw new BaseException(ResultCode.TASKMANAGE_IMAGING_AREA_REQUIRED);
            }
            if (dto.getNeedImaging() == 0) {
                dto.setImagingArea(null);
            }
        }

        String taskCode = UUID.randomUUID().toString().replace("-", "");
        tcTaskManagerMapper.insertTask(taskCode, dto, userId);
        Long taskId = tcTaskManagerMapper.selectLastInsertId();

        String attr = tcTaskManagerMapper.selectTemplateAttr(dto.getTemplateId());
        List<TemplateNode> nodes = new ArrayList<>();
        if (StringUtils.isNotBlank(attr)) {
            nodes = JSON.parseArray(attr, TemplateNode.class);
        }
        if (nodes.isEmpty()) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        Map<String, Long> map = new HashMap<>();
        for (TemplateNode n : nodes) {
            String rolesJson = JSON.toJSONString(n.getRoleIds());
            tcTaskManagerMapper.insertNodeInst(taskId, dto.getTemplateId(), n.getNodeId(), rolesJson, n.getOrderNo(), n.getMaxDuration(), userId);
            Long instId = tcTaskManagerMapper.selectLastInsertId();
            map.put(n.getKey(), instId);
        }
        for (TemplateNode n : nodes) {
            Long instId = map.get(n.getKey());
            List<Long> prevIds = n.getPrev() == null ? Collections.emptyList() : n.getPrev().stream().map(map::get).collect(Collectors.toList());
            List<Long> nextIds = n.getNext() == null ? Collections.emptyList() : n.getNext().stream().map(map::get).collect(Collectors.toList());
            tcTaskManagerMapper.updateNodeInstPrevNext(instId, JSON.toJSONString(prevIds), JSON.toJSONString(nextIds), userId);
        }

        if (dto.getResultDisplayNeeded() != null && dto.getResultDisplayNeeded() == 1) {
            List<Long> endIds = tcTaskManagerMapper.selectEndNodeInstIds(taskId);
            if (!endIds.isEmpty()) {
                NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
                if (viewNode != null) {
                    List<NodeRoleRel> rels = nodeRoleRelMapper.selectByNodeId(viewNode.getId());
                    List<String> roleIds = rels.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toList());
                    String prevNodeIds = JSON.toJSONString(endIds);
                    String handlerRoleIds = JSON.toJSONString(roleIds);
                    tcTaskManagerMapper.insertViewNodeInst(taskId, dto.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, userId);
                    Long viewInstId = tcTaskManagerMapper.selectLastInsertId();
                    if (viewInstId != null) {
                        tcTaskManagerMapper.appendViewNodeToEndNodes(viewInstId, endIds, userId);
                        List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                        for (String uid : userIds) {
                            tcTaskManagerMapper.insertWorkItem(taskId, viewInstId, uid, 0, userId);
                        }
                    }
                }
            }
        }

        for (TemplateNode n : nodes) {
            Long instId = map.get(n.getKey());
            boolean isStart = n.getPrev() == null || n.getPrev().isEmpty();
            if (isStart) {
                tcTaskManagerMapper.activateNodeInst(instId, userId);
            }
            List<String> roleIds = n.getRoleIds();
            if (roleIds != null && !roleIds.isEmpty()) {
                List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                int phaseStatus = isStart ? 1 : 0;
                for (String uid : userIds) {
                    tcTaskManagerMapper.insertWorkItem(taskId, instId, uid, phaseStatus, userId);
                }
            }
        }

        return taskId;
    }

    @Override
    public PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query) {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        query.setUserId(userId);

        boolean isAdmin = sysUserService.isAdmin(userId);
        TaskManagerTabEnum tab = TaskManagerTabEnum.fromValue(query.getTab());
        if (isAdmin) {
            if (tab != TaskManagerTabEnum.ALL) {
                throw new BaseException(ResultCode.TASKMANAGE_ADMIN_ONLY_ALL);
            }
        } else if (tab == TaskManagerTabEnum.ALL) {
            throw new BaseException(ResultCode.TASKMANAGE_ONLY_ADMIN_ALL);
        }

        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOffset((query.getPage() - 1) * query.getPageSize());

        List<TaskManagerListItemVO> vos = tcTaskManagerMapper.selectTaskList(query);
        if (!vos.isEmpty()) {
            // query current nodes
            List<Long> taskIds = vos.stream().map(TaskManagerListItemVO::getTaskId).collect(Collectors.toList());
            List<CurrentNodeRow> rows = tcTaskManagerMapper.selectCurrentNodes(taskIds);
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
            for (TaskManagerListItemVO vo : vos) {
                Map<Long, CurrentNodeVO> nodeMap = taskNodeMap.get(vo.getTaskId());
                if (nodeMap != null) {
                    vo.setCurrentNodes(new ArrayList<>(nodeMap.values()));
                } else {
                    vo.setCurrentNodes(new ArrayList<>());
                }
            }
        }
        long total = tcTaskManagerMapper.countTaskList(query);
        return new PageResult<>(query.getPage(), query.getPageSize(), (int) total, vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long taskId) {
        if (taskId == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        TaskManagerEditInfo info = tcTaskManagerMapper.selectTaskForEdit(taskId);
        if (info == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }

        boolean isAdmin = sysUserService.isAdmin(userId);
        if (!isAdmin && !userId.equals(info.getCreateBy())) {
            throw new BaseException(ResultCode.TASKMANAGE_NO_PERMISSION);
        }

        if (info.getStatus() == null || info.getStatus() != 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }

        Long doneCnt = tcTaskManagerMapper.countDoneNodeInst(taskId);
        if (doneCnt != null && doneCnt > 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }

        tcTaskManagerMapper.updateTaskCancel(taskId, userId);
        tcTaskManagerMapper.updateNodeInstCancel(taskId, userId);
        tcTaskManagerMapper.updateWorkItemCancel(taskId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editTask(TaskManagerEditDto dto) {
        if (dto == null || dto.getTaskId() == null) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        TaskManagerEditInfo info = tcTaskManagerMapper.selectTaskForEdit(dto.getTaskId());
        if (info == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }

        boolean isAdmin = sysUserService.isAdmin(userId);
        if (!isAdmin && !userId.equals(info.getCreateBy())) {
            throw new BaseException(ResultCode.TASKMANAGE_NO_PERMISSION);
        }

        if (info.getStatus() == null || info.getStatus() != 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_EDIT);
        }

        Long doneCnt = tcTaskManagerMapper.countDoneNodeInst(dto.getTaskId());
        if (doneCnt != null && doneCnt > 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_EDIT);
        }

        if (dto.getNeedImaging() != null) {
            if (dto.getNeedImaging() == 1 && StringUtils.isBlank(dto.getImagingArea())) {
                throw new BaseException(ResultCode.TASKMANAGE_IMAGING_AREA_REQUIRED);
            }
            if (dto.getNeedImaging() == 0) {
                dto.setImagingArea(null);
            }
        }
        tcTaskManagerMapper.updateTask(dto, userId);

        Integer oldDisplay = info.getResultDisplayNeeded();
        Integer newDisplay = dto.getResultDisplayNeeded();
        if (oldDisplay == null) {
            oldDisplay = 0;
        }
        if (newDisplay == null) {
            newDisplay = 0;
        }

        if (oldDisplay == 0 && newDisplay == 1) {
            List<Long> endIds = tcTaskManagerMapper.selectEndNodeInstIds(dto.getTaskId());
            if (!endIds.isEmpty()) {
                NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
                if (viewNode != null) {
                    List<NodeRoleRel> rels = nodeRoleRelMapper.selectByNodeId(viewNode.getId());
                    List<String> roleIds = rels.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toList());
                    String prevNodeIds = JSON.toJSONString(endIds);
                    String handlerRoleIds = JSON.toJSONString(roleIds);
                    tcTaskManagerMapper.insertViewNodeInst(dto.getTaskId(), info.getTemplateId(), viewNode.getId(), prevNodeIds, handlerRoleIds, userId);
                    Long viewInstId = tcTaskManagerMapper.selectLastInsertId();
                    if (viewInstId != null) {
                        tcTaskManagerMapper.appendViewNodeToEndNodes(viewInstId, endIds, userId);
                        List<String> userIds = tcTaskManagerMapper.selectUserIdsByRoleIds(roleIds);
                        for (String uid : userIds) {
                            tcTaskManagerMapper.insertWorkItem(dto.getTaskId(), viewInstId, uid, 0, userId);
                        }
                    }
                }
            }
        } else if (oldDisplay == 1 && newDisplay == 0) {
            NodeInfo viewNode = nodeInfoMapper.selectByName("查看影像结果");
            if (viewNode != null) {
                Long viewInstId = tcTaskManagerMapper.selectViewNodeInstId(dto.getTaskId(), viewNode.getId());
                if (viewInstId != null) {
                    tcTaskManagerMapper.deleteNodeInst(viewInstId, userId);
                    tcTaskManagerMapper.clearEndNodeNext(dto.getTaskId(), viewInstId, userId);
                    tcTaskManagerMapper.deleteWorkItemsByNodeInst(viewInstId, userId);
                }
            }
        }
    }
}
