package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.Func;
import com.zjlab.dataservice.modules.tc.mapper.TodoTemplateMapper;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTemplateQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTemplate;
import com.zjlab.dataservice.modules.tc.service.TcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjlab.dataservice.common.system.api.ISysBaseAPI;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcTemplateServiceImpl extends ServiceImpl<TodoTemplateMapper, TodoTemplate> implements
        TcTemplateService {

    @Autowired
    private ISysBaseAPI userService;  // 注入 UserService

    @Override
    public PageResult<TodoTemplateDto> qryTemplateList(TodoTemplateQueryDto todoTemplateQueryDto) {
        // 参数校验
        checkParam(todoTemplateQueryDto);
        IPage<TodoTemplateQueryDto> page = new Page<>(todoTemplateQueryDto.getPageNo(), todoTemplateQueryDto.getPageSize());

        // 查询数据
        IPage<TodoTemplateDto> resultPage = baseMapper.selectTodoTemplatePage(page, todoTemplateQueryDto);

        List<TodoTemplateDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        //整理返回结果格式
        PageResult<TodoTemplateDto> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    /**
     * 校验参数
     */
    private void checkParam(TodoTemplateQueryDto todoTemplateQueryDto) {
        todoTemplateQueryDto.setPageNo(Optional.ofNullable(todoTemplateQueryDto.getPageNo()).orElse(1));
        todoTemplateQueryDto.setPageSize(Optional.ofNullable(todoTemplateQueryDto.getPageSize()).orElse(50));
        String field = todoTemplateQueryDto.getOrderByField();
        if (StringUtils.isBlank(field)) {
            todoTemplateQueryDto.setOrderByField("update_time");
        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            todoTemplateQueryDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(todoTemplateQueryDto.getOrderByType())) {
            todoTemplateQueryDto.setOrderByType("desc");
        }
    }

    @Override
    public TodoTemplateDto submit(TodoTemplateDto todoTemplateDto) {
        // 统一code处理
        if (Func.isBlank(todoTemplateDto.getCode())) {
            todoTemplateDto.setCode(Func.randomUUID());
        }

        // 将 DTO 转为实体
        TodoTemplate template = new TodoTemplate();
        BeanUtil.copyProperties(todoTemplateDto, template);
        template.setTemplateAttr(todoTemplateDto.getAttrs());
        template.setFlag(1);

        // 查询是否存在同code + agentId 模板
        TodoTemplate query = new TodoTemplate();
        query.setCode(template.getCode());
        query.setAgentId(template.getAgentId());
        TodoTemplate existing = baseMapper.selectOneByTemplate(query);

        if (Func.notNull(existing)
                && existing.getTemplateName().equals(todoTemplateDto.getTemplateName())) {
            throw new BaseException(ResultCode.DUPLICATE_TEMPLATE_NAME);
        }

        if (Func.isBlank(template.getCode()) && Func.isNull(existing)) {
            throw new BaseException(ResultCode.QUERY_TEMPLATE_FAIL);
        }

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }

        LoginUser user = userService.getUserById(userId);

        if (Func.isNull(existing)) {
            // 新增
            template.setTemplateId(Func.randomUUID());
            template.setCreateTime(LocalDateTime.now());
            template.setUpdateTime(LocalDateTime.now());
            if (Func.notNull(user)) {
                template.setCreateBy(user.getUsername());
                template.setUpdateBy(user.getUsername());
            }
            log.info("before insert template info: {}", template);
            save(template);
        } else {
            // 更新
            BeanUtil.copyProperties(todoTemplateDto, existing); // 用新值覆盖旧值
            existing.setTemplateAttr(template.getTemplateAttr());
            existing.setUpdateTime(LocalDateTime.now());
            if (Func.notNull(user)) {
                existing.setUpdateBy(user.getUsername());
            }

            UpdateWrapper<TodoTemplate> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("code", template.getCode());
            log.info("before update template info: {}", existing);
            update(existing, updateWrapper);
        }

        return todoTemplateDto;
    }


    @Override
    public TodoTemplateDto findOne(TodoTemplateQueryDto todoTemplateQueryVO) {

        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateName())) {
            queryWrapper.eq("template_name", todoTemplateQueryVO.getTemplateName());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getAgentId())) {
            queryWrapper.eq("agent_id", todoTemplateQueryVO.getAgentId());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateType())){
            queryWrapper.eq("template_type", todoTemplateQueryVO.getTemplateType());
        }
        if (Func.isNotBlank(todoTemplateQueryVO.getTemplateCode())){
            queryWrapper.eq("code", todoTemplateQueryVO.getTemplateCode());
        }
        queryWrapper.eq("del_flag", 0)
                .orderByDesc("id")
                .last("limit 1");
        TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
        if (Func.isNull(todoTemplate)){
            return null;
        }
        TodoTemplateDto todoTemplateDto = new TodoTemplateDto();
        BeanUtil.copyProperties(todoTemplate, todoTemplateDto);
        return todoTemplateDto;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public TodoTemplateQueryDto deleteByCode(TodoTemplateQueryDto templateQueryVO) {

        List<String> templateCodes = templateQueryVO.getCodes();
        if (Func.notNull(templateCodes) && !templateCodes.isEmpty()){

            List<TodoTemplate> templates = new ArrayList<>();

            String userId = UserThreadLocal.getUserId();
            if (userId == null) {
                throw new BaseException(ResultCode.USERID_IS_NULL);
            }
            LoginUser user = userService.getUserById(userId);

            for (String code : templateCodes){
                QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("code", code).orderByDesc("id")
                        .last("limit 1");
                TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
                if (Func.notNull(todoTemplate)){
                    todoTemplate.setDelFlag(true);
                    LocalDateTime now = LocalDateTime.now();
                    todoTemplate.setUpdateTime(now);
                    if (Func.notNull(user)) {
                        todoTemplate.setUpdateBy(user.getUsername());
                    }
                    templates.add(todoTemplate);
                }
            }

            updateBatchById(templates);
        }
        return templateQueryVO;
    }

    @Override
    public TodoTemplateQueryDto deleteByTemplateId(@NotNull TodoTemplateQueryDto templateQueryVO) {

        String agentId = templateQueryVO.getAgentId();
        String templateId = templateQueryVO.getTemplateId();
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(agentId)) {
            queryWrapper.eq("agent_id", agentId);
        }
        if (Func.notNull(templateId)) {
            queryWrapper.eq("template_id", templateId);
        }
        queryWrapper.eq("code", templateQueryVO.getTemplateCode()).orderByDesc("id")
                .last("limit 1");
        TodoTemplate todoTemplate = baseMapper.selectOne(queryWrapper);
        if (Func.notNull(todoTemplate)){

            todoTemplate.setDelFlag(true);
            todoTemplate.setUpdateTime(now);
            todoTemplate.setUpdateBy(agentId);

            log.info("before update template info: {}", todoTemplate);
            updateById(todoTemplate);
        }
        return templateQueryVO;
    }

    @Override
    public PageResult<TodoTemplateDto> findByUserIdPage(TodoTemplateQueryDto basePage) {
        // 构造分页参数
        IPage<TodoTemplate> page = new Page<>(basePage.getPageNo(), basePage.getPageSize());

        // 构造查询条件
        QueryWrapper<TodoTemplate> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(basePage.getAgentId())) {
            queryWrapper.eq("agent_id", basePage.getAgentId());
        }
        if (Func.notNull(basePage.getTemplateCode())) {
            queryWrapper.eq("code", basePage.getTemplateCode());
        }
        if (Func.notNull(basePage.getTemplateType())) {
            queryWrapper.eq("template_type", basePage.getTemplateType());
        }
        if (Func.notNull(basePage.getUserId())) {
            queryWrapper.eq("create_by", basePage.getUserId());
        }

        // 查询分页数据
        IPage<TodoTemplate> resultPage = this.baseMapper.selectPage(page, queryWrapper);

        // 转换结果列表
        List<TodoTemplateDto> dtoList = resultPage.getRecords().stream().map(template -> {
            TodoTemplateDto dto = new TodoTemplateDto();
            BeanUtil.copyProperties(template, dto);
            return dto;
        }).collect(Collectors.toList());

        // 构造返回结果
        return buildPageResult(resultPage, dtoList);
    }

    // 封装分页结果
    private PageResult<TodoTemplateDto> buildPageResult(IPage<?> page, List<TodoTemplateDto> listRecords) {
        PageResult<TodoTemplateDto> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

}
