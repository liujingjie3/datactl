package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.mapper.SysUserMapper;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.modules.tc.mapper.NodeInfoMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeRoleRelMapper;
import com.zjlab.dataservice.modules.tc.mapper.TodoTemplateMapper;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.*;
import com.zjlab.dataservice.modules.tc.model.vo.NodeStatsVO;
import com.zjlab.dataservice.modules.tc.service.TcNodeService;
import com.zjlab.dataservice.modules.tc.enums.NodeActionTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 节点管理服务实现
 */
@Service
public class TcNodeServiceImpl implements TcNodeService {

    @Autowired
    private NodeInfoMapper nodeInfoMapper;
    @Autowired
    private NodeRoleRelMapper nodeRoleRelMapper;
    @Autowired
    private TodoTemplateMapper todoTemplateMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 检查节点是否关联任务模板
     */
    private boolean nodeReferencedByTemplate(Long nodeId) {
        List<TodoTemplate> templates = todoTemplateMapper.selectList(Wrappers.<TodoTemplate>lambdaQuery()
                .eq(TodoTemplate::getDelFlag, false));
        if (templates == null || templates.isEmpty()) {
            return false;
        }
        for (TodoTemplate template : templates) {
            Map<String, Object> attr = template.getTemplateAttr();
            if (attr == null) {
                continue;
            }
            Object nodesObj = attr.get("nodes");
            if (!(nodesObj instanceof List)) {
                continue;
            }
            for (Object nodeObj : (List<?>) nodesObj) {
                if (!(nodeObj instanceof Map)) {
                    continue;
                }
                Map<?, ?> nodeMap = (Map<?, ?>) nodeObj;
                Object propertiesObj = nodeMap.get("properties");
                if (!(propertiesObj instanceof Map)) {
                    continue;
                }
                Map<?, ?> propertiesMap = (Map<?, ?>) propertiesObj;
                Object cfgObj = propertiesMap.get("nodeConfigParams");
                if (!(cfgObj instanceof Map)) {
                    continue;
                }
                Object idObj = ((Map<?, ?>) cfgObj).get("id");
                if (idObj != null) {
                    try {
                        long id = Long.parseLong(String.valueOf(idObj));
                        if (id == nodeId) {
                            return true;
                        }
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        }
        return false;
    }

    /**
     * 关联校验
     */
    private void ensureNodeNotReferenced(Long nodeId) {
        if (nodeReferencedByTemplate(nodeId)) {
            throw new BaseException(ResultCode.NODE_ASSOCIATED_TEMPLATE);
        }
    }

    /**
     * 统计指定类型操作数量
     */
    private long countActionType(List<NodeActionDto> actions, NodeActionTypeEnum type) {
        if (actions == null || actions.isEmpty() || type == null) {
            return 0L;
        }
        return actions.stream()
                .filter(action -> action != null
                        && action.getType() != null
                        && action.getType() == type.getCode())
                .count();
    }

    /**
     * 校验节点操作配置
     */
    private void validateNodeActions(List<NodeActionDto> actions) {
        long decisionCount = countActionType(actions, NodeActionTypeEnum.DECISION);
        if (decisionCount != 1) {
            throw new BaseException(ResultCode.NODE_ONLY_ONE_DECISION_ACTION);
        }
        if (countActionType(actions, NodeActionTypeEnum.MODIFY_REMOTE_CMD) > 1) {
            throw new BaseException(ResultCode.NODE_ONLY_ONE_MODIFY_REMOTE_CMD_ACTION);
        }
        if (countActionType(actions, NodeActionTypeEnum.SELECT_ORBIT_PLAN) > 1) {
            throw new BaseException(ResultCode.NODE_ONLY_ONE_SELECT_ORBIT_ACTION);
        }
        if (countActionType(actions, NodeActionTypeEnum.UPLOAD) > 1) {
            throw new BaseException(ResultCode.NODE_ONLY_ONE_UPLOAD_ACTION);
        }
    }

    /**
     * 统计节点数量。
     *
     * @return 节点统计信息
     */
    @Override
    public NodeStatsVO getStats() {
        // 统计启用、停用及总数量
        long active = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0).eq(NodeInfo::getStatus, 1));
        long disabled = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0).eq(NodeInfo::getStatus, 0));
        long total = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0));
        return new NodeStatsVO((int) active, (int) disabled, (int) total);
    }

    /**
     * 根据条件分页查询节点。
     *
     * @param queryDto 查询条件
     * @return 节点分页数据
     */
    @Override
    public PageResult<NodeInfoDto> listNodes(NodeQueryDto queryDto) {
        // 1. 构建分页与查询条件
        Page<NodeInfo> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        LambdaQueryWrapper<NodeInfo> wrapper = Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0)
                .orderByDesc(NodeInfo::getCreateTime);
        if (queryDto.getStatus() != null) {
            wrapper.eq(NodeInfo::getStatus, queryDto.getStatus());
        }
        if (StringUtils.isNotBlank(queryDto.getName())) {
            wrapper.like(NodeInfo::getName, queryDto.getName());
        }
        if (StringUtils.isNotBlank(queryDto.getRoleId())) {
            List<Long> nodeIds = nodeRoleRelMapper.selectList(Wrappers.<NodeRoleRel>lambdaQuery()
                            .eq(NodeRoleRel::getDelFlag, 0)
                            .eq(NodeRoleRel::getRoleId, queryDto.getRoleId()))
                    .stream().map(NodeRoleRel::getNodeId).collect(Collectors.toList());
            if (nodeIds.isEmpty()) {
                return new PageResult<>(queryDto.getPageNo(), queryDto.getPageSize(), 0, Collections.emptyList());
            }
            wrapper.in(NodeInfo::getId, nodeIds);
        }

        // 2. 执行查询并转换为DTO
        IPage<NodeInfo> nodePage = nodeInfoMapper.selectPage(page, wrapper);
        List<NodeInfoDto> dtoList = nodePage.getRecords().stream().map(node -> {
            NodeInfoDto dto = new NodeInfoDto();
            BeanUtil.copyProperties(node, dto);

            // 2.1 填充角色列表
            List<NodeRoleRel> roleRels = nodeRoleRelMapper.selectByNodeId(node.getId());
            if (roleRels != null && !roleRels.isEmpty()) {
                List<NodeRoleDto> roles = roleRels.stream().map(rel -> {
                    NodeRoleDto r = new NodeRoleDto();
                    r.setRoleId(rel.getRoleId());
                    SysRole role = sysRoleMapper.selectById(rel.getRoleId());
                    if (role != null) {
                        r.setRoleName(role.getRoleName());
                    }
                    return r;
                }).collect(Collectors.toList());
                dto.setRoles(roles);

            }
            // 2.2 简化并设置操作列表
            if (StringUtils.isNotBlank(node.getActions())) {
                List<NodeActionDto> actions = JSON.parseArray(node.getActions(), NodeActionDto.class);
                if (actions != null) {
                    List<NodeActionDto> simple = actions.stream().map(a -> {
                        NodeActionDto na = new NodeActionDto();
                        na.setType(a.getType());
                        na.setName(a.getName());
                        return na;
                    }).collect(Collectors.toList());
                    dto.setActions(simple);
                }
            }
            // 2.3 填充创建者和更新者信息
            dto.setCreatorId(node.getCreateBy());
            SysUser user = sysUserMapper.selectById(node.getCreateBy());
            if (user != null) {
                dto.setCreatorName(user.getRealname());
            }
            dto.setCreateTime(node.getCreateTime());
            dto.setUpdaterId(node.getUpdateBy());
            SysUser upUser = sysUserMapper.selectById(node.getUpdateBy());
            if (upUser != null) {
                dto.setUpdaterName(upUser.getRealname());
            }
            dto.setUpdateTime(node.getUpdateTime());
            return dto;
        }).collect(Collectors.toList());

        // 3. 返回分页结果
        return new PageResult<NodeInfoDto>(
                (int) nodePage.getCurrent(),
                (int) nodePage.getSize(),
                (int) nodePage.getTotal(),
                dtoList);

    }

    /**
     * 创建节点并保存角色关系。
     *
     * @param dto 节点信息
     * @return 新节点ID
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Long createNode(NodeInfoDto dto) {
        // 1. 获取当前用户并校验
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        if (!sysUserService.isAdmin(userId)) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }
        if (dto == null
                || StringUtils.isBlank(dto.getName())
                || dto.getStatus() == null
                || dto.getRoles() == null
                || dto.getRoles().isEmpty()
                || dto.getExpectedDuration() == null || dto.getExpectedDuration() < 0
                || dto.getTimeoutRemind() == null || dto.getTimeoutRemind() < 0) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        validateNodeActions(dto.getActions());

        // 2. 转换DTO为实体并补充通用字段
        NodeInfo node = new NodeInfo();
        BeanUtil.copyProperties(dto, node);
        node.setDelFlag(0);
        node.setCreateBy(userId);
        node.setUpdateBy(userId);
        if (dto.getActions() != null) {
            node.setActions(JSON.toJSONString(dto.getActions()));
        }
        nodeInfoMapper.insert(node);

        // 3. 保存节点与角色的关联关系
        for (NodeRoleDto roleDto : dto.getRoles()) {
            NodeRoleRel rel = new NodeRoleRel();
            rel.setNodeId(node.getId());
            rel.setRoleId(roleDto.getRoleId());
            rel.setDelFlag(0);
            rel.setCreateBy(userId);
            rel.setUpdateBy(userId);
            nodeRoleRelMapper.insert(rel);
        }

        // 4. 返回新节点ID
        return node.getId();
    }

    /**
     * 更新节点及其角色配置。
     *
     * @param id  节点ID
     * @param dto 节点信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateNode(Long id, NodeInfoDto dto) {
        // 1. 获取当前用户并校验
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        if (!sysUserService.isAdmin(userId)) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }
        if (dto == null
                || StringUtils.isBlank(dto.getName())
                || dto.getStatus() == null
                || dto.getRoles() == null
                || dto.getRoles().isEmpty()
                || dto.getExpectedDuration() == null || dto.getExpectedDuration() < 0
                || dto.getTimeoutRemind() == null || dto.getTimeoutRemind() < 0) {
            throw new BaseException(ResultCode.PARA_ERROR);
        }

        validateNodeActions(dto.getActions());

        NodeInfo exist = nodeInfoMapper.selectById(id);
        if (exist != null && exist.getStatus() == 1 && dto.getStatus() == 0) {
            ensureNodeNotReferenced(id);
        }

        // 2. 更新节点基本信息
        NodeInfo node = new NodeInfo();
        BeanUtil.copyProperties(dto, node);
        node.setId(id);
        node.setUpdateBy(userId);
        if (dto.getActions() != null) {
            node.setActions(JSON.toJSONString(dto.getActions()));
        }
        nodeInfoMapper.updateById(node);

        // 3. 更新角色关系，只有在变更时才调整
        List<NodeRoleRel> existing = nodeRoleRelMapper.selectByNodeId(id);
        Set<String> oldIds = existing.stream().map(NodeRoleRel::getRoleId).collect(Collectors.toSet());
        Set<String> newIds = dto.getRoles().stream().map(NodeRoleDto::getRoleId).collect(Collectors.toSet());
        if (!oldIds.equals(newIds)) {
            nodeRoleRelMapper.deleteByNodeId(id);
            for (NodeRoleDto roleDto : dto.getRoles()) {
                NodeRoleRel rel = new NodeRoleRel();
                rel.setNodeId(id);
                rel.setRoleId(roleDto.getRoleId());
                rel.setDelFlag(0);
                rel.setCreateBy(userId);
                rel.setUpdateBy(userId);
                nodeRoleRelMapper.insert(rel);
            }
        }
    }

    /**
     * 修改节点状态。
     *
     * @param id 节点ID
     * @param status 状态值
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        if (!sysUserService.isAdmin(userId)) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }
        NodeInfo exist = nodeInfoMapper.selectById(id);
        if (exist != null && status == 0 && exist.getStatus() != 0) {
            ensureNodeNotReferenced(id);
        }
        // 简单更新状态字段
        NodeInfo node = new NodeInfo();
        node.setId(id);
        node.setStatus(status);
        nodeInfoMapper.updateById(node);
    }

    /**
     * 删除节点及其角色关系。
     *
     * @param id 节点ID
     */
    @Override
    public void deleteNode(Long id) {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        if (!sysUserService.isAdmin(userId)) {
            throw new BaseException(ResultCode.SC_JEECG_NO_AUTHZ);
        }
        ensureNodeNotReferenced(id);
        // 删除节点及关联的角色关系
        nodeInfoMapper.deleteById(id);
        nodeRoleRelMapper.deleteByNodeId(id);
    }

    /**
     * 查询节点详情。
     *
     * @param id 节点ID
     * @return 节点详细信息
     */
    @Override
    public NodeInfoDto getDetail(Long id) {
        // 1. 查询节点实体
        NodeInfo node = nodeInfoMapper.selectById(id);
        if (node == null || node.getDelFlag() == 1) {
            return null;
        }
        NodeInfoDto dto = new NodeInfoDto();
        BeanUtil.copyProperties(node, dto);

        // 2. 填充角色列表
        List<NodeRoleRel> roleRels = nodeRoleRelMapper.selectByNodeId(id);
        if (roleRels != null && !roleRels.isEmpty()) {
            List<NodeRoleDto> roles = roleRels.stream().map(rel -> {
                NodeRoleDto r = new NodeRoleDto();
                r.setRoleId(rel.getRoleId());
                SysRole role = sysRoleMapper.selectById(rel.getRoleId());
                if (role != null) {
                    r.setRoleName(role.getRoleName());
                }
                return r;
            }).collect(Collectors.toList());
            dto.setRoles(roles);

        }
        // 3. 设置操作列表
        if (StringUtils.isNotBlank(node.getActions())) {
            List<NodeActionDto> actions = JSON.parseArray(node.getActions(), NodeActionDto.class);
            dto.setActions(actions);
        }
        // 4. 填充创建者和更新者信息
        dto.setCreatorId(node.getCreateBy());
        SysUser creator = sysUserMapper.selectById(node.getCreateBy());
        if (creator != null) {
            dto.setCreatorName(creator.getRealname());
        }
        dto.setUpdaterId(node.getUpdateBy());
        SysUser upUser = sysUserMapper.selectById(node.getUpdateBy());
        if (upUser != null) {
            dto.setUpdaterName(upUser.getRealname());
        }
        return dto;
    }
}
