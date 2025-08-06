package com.zjlab.dataservice.modules.toolbox.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.UUIDGenerator;
import com.zjlab.dataservice.modules.toolbox.dto.FlowTemplateDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.enumerate.TemplateType;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import com.zjlab.dataservice.modules.toolbox.mapper.FlowTemplateMapper;
import com.zjlab.dataservice.modules.toolbox.po.FlowTemplatePo;
import com.zjlab.dataservice.modules.toolbox.po.TaskPo;
import com.zjlab.dataservice.modules.toolbox.service.FlowTemplateService;
import com.zjlab.dataservice.modules.toolbox.vo.FlowTemplateVo;
import com.zjlab.dataservice.modules.toolbox.vo.FunctionModuleVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlowTemplateServiceImpl extends ServiceImpl<FlowTemplateMapper, FlowTemplatePo> implements FlowTemplateService {

    private final String ADMIN = "admin";

    @Override
    public PageResult<FlowTemplateVo> listMyTemplate(ToolBoxDto toolBoxDto) {
        PageResult<FlowTemplateVo> result = new PageResult<>();
        String userId = checkUserIdExist();
        IPage<FlowTemplatePo> page = new Page<>(Optional.ofNullable(toolBoxDto.getPageNo()).orElse(1), Optional.ofNullable(toolBoxDto.getPageSize()).orElse(10));
        LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
        query.eq(FlowTemplatePo::getUserId, userId);
        query.notIn(FlowTemplatePo::getStatus, ToolBoxStatus.DELETE);
        if (StringUtil.isNotBlank(toolBoxDto.getTemplateName())) {
            query.like(FlowTemplatePo::getTemplateName, toolBoxDto.getTemplateName());
        }
        IPage<FlowTemplatePo> templateListByUser = baseMapper.selectPage(page, query);

        result.setPageNo((int) templateListByUser.getCurrent());
        result.setPageSize((int) templateListByUser.getSize());
        result.setTotal((int) templateListByUser.getTotal());
        List<FlowTemplatePo> flowTemplatePoList = templateListByUser.getRecords();
        // 过滤掉已删除模板不显示
        result.setData(flowTemplatePoList.stream().map(this::po2vo).collect(Collectors.toList()));
        return result;
    }

    @Override
    public PageResult<FlowTemplateVo> listPublicTemplate(ToolBoxDto toolBoxDto) {
        PageResult<FlowTemplateVo> result = new PageResult<>();
        IPage<FlowTemplatePo> page = new Page<>(Optional.ofNullable(toolBoxDto.getPageNo()).orElse(1), Optional.ofNullable(toolBoxDto.getPageSize()).orElse(10));
        LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
        query.eq(FlowTemplatePo::getUserId, ADMIN);
        query.notIn(FlowTemplatePo::getStatus, ToolBoxStatus.DELETE);
        if (StringUtil.isNotBlank(toolBoxDto.getTemplateName())) {
            query.like(FlowTemplatePo::getTemplateName, toolBoxDto.getTemplateName());
        }
        IPage<FlowTemplatePo> templateListByAdmin = baseMapper.selectPage(page, query);
        result.setPageNo((int) templateListByAdmin.getCurrent());
        result.setPageSize((int) templateListByAdmin.getSize());
        result.setTotal((int) templateListByAdmin.getTotal());
        List<FlowTemplatePo> flowTemplatePoList = templateListByAdmin.getRecords();
        // 过滤掉已删除模板不显示
        result.setData(flowTemplatePoList.stream().map(this::po2vo).collect(Collectors.toList()));
        return result;
    }

    @Override
    public PageResult<FlowTemplateVo> listAllTemplate(ToolBoxDto toolBoxDto) {
        PageResult<FlowTemplateVo> result = new PageResult<>();
        IPage<FlowTemplatePo> page = new Page<>(Optional.ofNullable(toolBoxDto.getPageNo()).orElse(1), Optional.ofNullable(toolBoxDto.getPageSize()).orElse(10));
        LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
        if (StringUtil.isNotBlank(toolBoxDto.getTemplateName())) {
            query.like(FlowTemplatePo::getTemplateName, toolBoxDto.getTemplateName());
        }
        if (toolBoxDto.getTemplateType() != null) {
            query.eq(FlowTemplatePo::getTemplateType, toolBoxDto.getTemplateType().getCode());
        }
        IPage<FlowTemplatePo> templateListByAdmin = baseMapper.selectPage(page, query);
        result.setPageNo((int) templateListByAdmin.getCurrent());
        result.setPageSize((int) templateListByAdmin.getSize());
        result.setTotal((int) templateListByAdmin.getTotal());
        List<FlowTemplatePo> flowTemplatePoList = templateListByAdmin.getRecords();
        // 过滤掉已删除模板不显示
        flowTemplatePoList.removeIf(taskPo -> taskPo.getStatus().getCode() == 7);
        result.setData(flowTemplatePoList.stream().map(this::po2vo).collect(Collectors.toList()));
        return result;
    }

    @Override
    public FlowTemplateVo createTemplate(FlowTemplateDto flowTemplateDto) {
        FlowTemplateVo result = new FlowTemplateVo();
        String userId = checkUserIdExist();

        String templateName = flowTemplateDto.getTemplateName();
        if (StringUtil.isNotBlank(templateName)) {
            LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
            query.eq(FlowTemplatePo::getTemplateName, templateName);
            // 排除已删除的条目
            query.notIn(FlowTemplatePo::getStatus, ToolBoxStatus.DELETE);
            FlowTemplatePo flowTemplatePo = baseMapper.selectOne(query);
            if (flowTemplatePo != null) {
                return result;
            } else {
                flowTemplatePo = new FlowTemplatePo();
                BeanUtils.copyProperties(flowTemplateDto, flowTemplatePo);
                flowTemplatePo.setTemplateContent(flowTemplateDto.getTemplateContent().toString());
                String uuid = UUIDGenerator.generate();
                flowTemplatePo.setTemplateId(uuid);
                flowTemplatePo.setUserId(userId);
                flowTemplatePo.setStatus(ToolBoxStatus.READY);
                flowTemplatePo.setTemplateType(TemplateType.CUSTOM);
                baseMapper.insert(flowTemplatePo);
                BeanUtils.copyProperties(flowTemplatePo, result);
                result.setTemplateContent(flowTemplateDto.getTemplateContent());
            }
        }
        return result;
    }

    @Override
    public FlowTemplateVo updateTemplateById(FlowTemplateDto flowTemplateDto) {
        FlowTemplateVo result = new FlowTemplateVo();
        String userId = checkUserIdExist();

        String templateName = flowTemplateDto.getTemplateName();
        String templateId = flowTemplateDto.getTemplateId();

        if (StringUtil.isNotBlank(templateId)) {
            LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
            query.eq(FlowTemplatePo::getTemplateId, templateId);
            FlowTemplatePo flowTemplatePo = baseMapper.selectOne(query);
            if (flowTemplatePo == null) {
                log.info("the flow template to update is not found: " + templateId);
                return result;
            } else {
                // 先检查名字修改
                if (!Objects.equals(flowTemplateDto.getTemplateName(), flowTemplatePo.getTemplateName())) {
                    LambdaQueryWrapper<FlowTemplatePo> queryByName = new LambdaQueryWrapper<>();
                    queryByName.eq(FlowTemplatePo::getTemplateName, templateName);
                    // 排除已删除的条目
                    query.notIn(FlowTemplatePo::getStatus, ToolBoxStatus.DELETE);
                    FlowTemplatePo flowTemplatePoByName = baseMapper.selectOne(queryByName);
                    // 出现重名模板，抛异常
                    if (flowTemplatePoByName != null) {
                        log.info("duplicate template name, id: " + templateId);
                        return result;
                    }
                }
                flowTemplatePo = new FlowTemplatePo();
                BeanUtils.copyProperties(flowTemplateDto, flowTemplatePo);
                flowTemplatePo.setTemplateContent(flowTemplateDto.getTemplateContent());
                flowTemplatePo.setUserId(userId);
                LambdaQueryWrapper<FlowTemplatePo> updateQuery = new LambdaQueryWrapper<>();
                updateQuery.eq(FlowTemplatePo::getTemplateId, templateId);
                int update = baseMapper.update(flowTemplatePo, updateQuery);

                if (update != 1) {
                    return result;
                } else {
                    BeanUtils.copyProperties(flowTemplatePo, result);
                    result.setTemplateContent(flowTemplateDto.getTemplateContent());
                }
            }
        }
        return result;
    }

    @Override
    public FlowTemplateVo queryTemplateById(FlowTemplateDto flowTemplateDto) {
        FlowTemplateVo result = new FlowTemplateVo();
        String userId = checkUserIdExist();

        String templateId = flowTemplateDto.getTemplateId();
        if (StringUtil.isNotBlank(templateId)) {
            LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
            query.eq(FlowTemplatePo::getTemplateId, templateId);
            // todo: 关于模板权限的问题
//            query.eq(FlowTemplatePo::getUserId, userId);
            FlowTemplatePo flowTemplatePo = baseMapper.selectOne(query);
            if (flowTemplatePo == null) {
                return result;
            } else {
                BeanUtils.copyProperties(flowTemplatePo, result);
            }
        }
        return result;
    }

    public FlowTemplatePo queryTemplateById(String templateId) {
        FlowTemplatePo result = new FlowTemplatePo();
        String userId = checkUserIdExist();

        if (StringUtil.isNotBlank(templateId)) {
            LambdaQueryWrapper<FlowTemplatePo> query = new LambdaQueryWrapper<>();
            query.eq(FlowTemplatePo::getTemplateId, templateId);
            // todo: 关于模板权限的问题
//            query.eq(FlowTemplatePo::getUserId, userId);
            return baseMapper.selectOne(query);
        }
        return result;
    }

    @Override
    public List<FunctionModuleVo> listFunctionNodes() {



        return null;
    }

    private String checkUserIdExist(){
        String userId = UserThreadLocal.getUserId();
        if (userId == null){
            userId = "1589798185264324609";
            // throw new BaseException(ResultCode.INTERNAL_SERVER_ERROR);
        }
        return userId;
    }

    private FlowTemplateVo po2vo(FlowTemplatePo flowTemplatePo) {
        FlowTemplateVo flowTemplateVo = new FlowTemplateVo();
        BeanUtils.copyProperties(flowTemplatePo, flowTemplateVo);
        flowTemplateVo.setTemplateContent(flowTemplatePo.getTemplateContent());
        return flowTemplateVo;
    }
}
