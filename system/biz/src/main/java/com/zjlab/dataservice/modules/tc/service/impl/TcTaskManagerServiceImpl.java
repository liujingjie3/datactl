package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.modules.tc.mapper.TcTaskManagerMapper;
import com.zjlab.dataservice.modules.tc.model.dto.CurrentNodeRow;
import com.zjlab.dataservice.modules.tc.model.dto.NodeRoleDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query) {
        //todo ,对于管理员看所有任务，因为还没有确认如何判断管理员账号，这里暂时还没有做筛选，就是都能看到所有任务

        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof LoginUser) {
            query.setUserId(((LoginUser) principal).getId());
        }

        if (StringUtils.isBlank(query.getUserId())) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
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
}
