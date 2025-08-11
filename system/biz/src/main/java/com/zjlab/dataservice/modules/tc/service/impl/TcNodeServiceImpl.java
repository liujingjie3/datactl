package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.system.entity.SysRole;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.mapper.SysRoleMapper;
import com.zjlab.dataservice.modules.system.mapper.SysUserMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeInfoMapper;
import com.zjlab.dataservice.modules.tc.mapper.NodeRoleRelMapper;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.*;
import com.zjlab.dataservice.modules.tc.service.TcNodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.List;
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
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public NodeStatsVo getStats() {
        long active = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0).eq(NodeInfo::getStatus, 1));
        long disabled = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0).eq(NodeInfo::getStatus, 0));
        long total = nodeInfoMapper.selectCount(Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0));
        return new NodeStatsVo((int) active, (int) disabled, (int) total);
    }

    @Override
    public PageResult<NodeInfoDto> listNodes(NodeQueryDto queryDto) {
        Page<NodeInfo> page = new Page<>(queryDto.getPage(), queryDto.getPageSize());
        LambdaQueryWrapper<NodeInfo> wrapper = Wrappers.<NodeInfo>lambdaQuery()
                .eq(NodeInfo::getDelFlag, 0);
        if (queryDto.getStatus() != null) {
            wrapper.eq(NodeInfo::getStatus, queryDto.getStatus());
        }
        if (StringUtils.isNotBlank(queryDto.getName())) {
            wrapper.like(NodeInfo::getName, queryDto.getName());
        }
        if (StringUtils.isNotBlank(queryDto.getCreatorName())) {
            List<SysUser> users = sysUserMapper.selectList(Wrappers.<SysUser>query()
                    .like("username", queryDto.getCreatorName()));
            if (users.isEmpty()) {
                return new PageResult<>(queryDto.getPage(), queryDto.getPageSize(), 0, Collections.emptyList());
            }
            List<Long> userIds = users.stream().map(u -> Long.valueOf(u.getId())).collect(Collectors.toList());
            wrapper.in(NodeInfo::getCreateBy, userIds);
        }
        if (StringUtils.isNotBlank(queryDto.getRoleName())) {
            List<SysRole> roles = sysRoleMapper.selectList(Wrappers.<SysRole>query()
                    .like("role_name", queryDto.getRoleName()));
            if (roles.isEmpty()) {
                return new PageResult<>(queryDto.getPage(), queryDto.getPageSize(), 0, Collections.emptyList());
            }
            List<Long> roleIds = roles.stream().map(r -> Long.valueOf(r.getId())).collect(Collectors.toList());
            List<Long> nodeIds = nodeRoleRelMapper.selectList(Wrappers.<NodeRoleRel>lambdaQuery()
                            .eq(NodeRoleRel::getDelFlag, 0)
                            .in(NodeRoleRel::getRoleId, roleIds))
                    .stream().map(NodeRoleRel::getNodeId).collect(Collectors.toList());
            if (nodeIds.isEmpty()) {
                return new PageResult<>(queryDto.getPage(), queryDto.getPageSize(), 0, Collections.emptyList());
            }
            wrapper.in(NodeInfo::getId, nodeIds);
        }

        IPage<NodeInfo> nodePage = nodeInfoMapper.selectPage(page, wrapper);
        List<NodeInfoDto> dtoList = nodePage.getRecords().stream().map(node -> {
            NodeInfoDto dto = new NodeInfoDto();
            BeanUtil.copyProperties(node, dto);

            // roles
            List<NodeRoleRel> roleRels = nodeRoleRelMapper.selectByNodeId(node.getId());
            if (roleRels != null && !roleRels.isEmpty()) {
                List<NodeRoleDto> roles = roleRels.stream().map(rel -> {
                    NodeRoleDto r = new NodeRoleDto();
                    r.setRoleId(rel.getRoleId());
                    SysRole role = sysRoleMapper.selectById(String.valueOf(rel.getRoleId()));
                    if (role != null) {
                        r.setRoleName(role.getRoleName());
                    }
                    return r;
                }).collect(Collectors.toList());
                dto.setRoles(roles);

            }
            // actions
            if (StringUtils.isNotBlank(node.getActions())) {
                List<NodeActionDto> actions = JSON.parseArray(node.getActions(), NodeActionDto.class);
                if (actions != null) {
                    List<String> names = actions.stream().map(NodeActionDto::getName).collect(Collectors.toList());
                    dto.setActionTypes(names);
                }
            }
            // creator
            dto.setCreatorId(node.getCreateBy());
            SysUser user = sysUserMapper.selectById(String.valueOf(node.getCreateBy()));
            if (user != null) {
                dto.setCreatorName(user.getUsername());
            }
            dto.setCreateTime(node.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<NodeInfoDto>(
                (int) nodePage.getCurrent(),
                (int) nodePage.getSize(),
                (int) nodePage.getTotal(),
                dtoList);

    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Long createNode(NodeInfoDto dto) {
        //todo getLoginUser获取用户信息，用于填写创建人和更新人
        NodeInfo node = new NodeInfo();
        BeanUtil.copyProperties(dto, node);
        node.setDelFlag(0);
        if (dto.getActions() != null) {
            node.setActions(JSON.toJSONString(dto.getActions()));
        }
        nodeInfoMapper.insert(node);

        if (dto.getRoles() != null) {
            for (NodeRoleDto roleDto : dto.getRoles()) {
                NodeRoleRel rel = new NodeRoleRel();
                rel.setNodeId(node.getId());
                rel.setRoleId(roleDto.getRoleId());
                rel.setDelFlag(0);
                nodeRoleRelMapper.insert(rel);
            }
        }

        return node.getId();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateNode(Long id, NodeInfoDto dto) {
        NodeInfo node = new NodeInfo();
        BeanUtil.copyProperties(dto, node);
        node.setId(id);
        if (dto.getActions() != null) {
            node.setActions(JSON.toJSONString(dto.getActions()));
        }
        nodeInfoMapper.updateById(node);
        nodeRoleRelMapper.deleteByNodeId(id);

        //todo 判断一下，如果角色变了，就删了重新插入，如果没有变，就不修改，下面的操作也是一样
        if (dto.getRoles() != null) {
            for (NodeRoleDto roleDto : dto.getRoles()) {
                NodeRoleRel rel = new NodeRoleRel();
                rel.setNodeId(id);
                rel.setRoleId(roleDto.getRoleId());
                rel.setDelFlag(0);
                nodeRoleRelMapper.insert(rel);
            }
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        NodeInfo node = new NodeInfo();
        node.setId(id);
        node.setStatus(status);
        nodeInfoMapper.updateById(node);
    }

    @Override
    public void deleteNode(Long id) {
        nodeInfoMapper.deleteById(id);
        nodeRoleRelMapper.deleteByNodeId(id);
    }

    @Override
    public NodeInfoDto getDetail(Long id) {
        NodeInfo node = nodeInfoMapper.selectById(id);
        if (node == null || node.getDelFlag() == 1) {
            return null;
        }
        NodeInfoDto dto = new NodeInfoDto();
        BeanUtil.copyProperties(node, dto);

        List<NodeRoleRel> roleRels = nodeRoleRelMapper.selectByNodeId(id);
        if (roleRels != null && !roleRels.isEmpty()) {
            List<NodeRoleDto> roles = roleRels.stream().map(rel -> {
                NodeRoleDto r = new NodeRoleDto();
                r.setRoleId(rel.getRoleId());
                SysRole role = sysRoleMapper.selectById(String.valueOf(rel.getRoleId()));
                if (role != null) {
                    r.setRoleName(role.getRoleName());
                }
                return r;
            }).collect(Collectors.toList());
            dto.setRoles(roles);

        }
        if (StringUtils.isNotBlank(node.getActions())) {
            List<NodeActionDto> actions = JSON.parseArray(node.getActions(), NodeActionDto.class);
            dto.setActions(actions);
        }
        return dto;
    }
}
