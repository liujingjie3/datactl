package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskManagerMapper;
import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerTabEnum;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.mapper.SysUserMapper;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private SysUserMapper sysUserMapper;

    @Autowired
    private ISysUserService sysUserService;

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

        SysUser user = sysUserMapper.selectById(userId);

        if (user != null) {
            query.setUserName(user.getUsername());
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

        Integer status = tcTaskManagerMapper.selectTaskStatus(taskId);
        if (status == null) {
            throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
        }
        if (status != 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }

        Long doneCnt = tcTaskManagerMapper.countDoneNodeInst(taskId);
        if (doneCnt != null && doneCnt > 0) {
            throw new BaseException(ResultCode.TASKMANAGE_CANNOT_CANCEL);
        }

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        String userName = null;
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            userName = user.getUsername();
        }

        tcTaskManagerMapper.updateTaskCancel(taskId, userName);
        tcTaskManagerMapper.updateNodeInstCancel(taskId, userName);
        tcTaskManagerMapper.updateWorkItemCancel(taskId, userName);
    }
}
