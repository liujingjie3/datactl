package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.system.api.ISysBaseAPI;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.Func;
import com.zjlab.dataservice.modules.tc.common.CommonUtil;
import com.zjlab.dataservice.modules.tc.common.Constants;
import com.zjlab.dataservice.modules.tc.event.*;
import com.zjlab.dataservice.modules.tc.mapper.TodoInstanceMapper;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.MsgInstance;
import com.zjlab.dataservice.modules.tc.model.entity.TodoInstance;
import com.zjlab.dataservice.modules.tc.model.entity.TodoRead;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import com.zjlab.dataservice.modules.tc.service.TcInstanceService;
import com.zjlab.dataservice.modules.tc.service.TcReadService;
import com.zjlab.dataservice.modules.tc.service.TcTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TcInstanceServiceImpl extends ServiceImpl<TodoInstanceMapper, TodoInstance> implements
        TcInstanceService {

    @Autowired
    private TcApplicationListener applicationContext;

    @Autowired
    private ISysBaseAPI userService;

    @Lazy
    @Autowired
    private TcTaskService taskService;

    @Lazy
    @Autowired
    private TcReadService readService;

    @Override
    public PageResult<TodoInstanceDto> selectTodoInstancePage(TodoInstanceDto todoInstance) {
        // 参数校验
        CommonUtil.checkPageAndOrderParam(todoInstance);
        IPage<TodoInstanceDto> page = new Page<>(todoInstance.getPageNo(), todoInstance.getPageSize());

        // 查询数据
        IPage<TodoInstanceDto> resultPage = baseMapper.selectTodoInstancePage(page, todoInstance);
        List<TodoInstanceDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TodoInstanceDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;
    }

    @Override
    public TodoInstanceDto createInstance(TodoInstanceDto instanceVO) {

        TodoInstance instance = new TodoInstance();
        BeanUtil.copyProperties(instanceVO, instance);
        instance.setCreateBy(instanceVO.getFromUserId());

        String code = instance.getCode();

        TodoInstance todoInstance = this.baseMapper.selectOneByInstance(instance);
        if (Func.notNull(todoInstance) && Func.notNull(todoInstance.getId())){
            throw new BaseException(ResultCode.INSTANCE_IS_EXISTS);
        }
        if (Func.isNotEmpty(instanceVO.getAttrs())){
            instance.setInstanceAttr(Func.toJson(instanceVO.getAttrs()));
        }
        instance.setInstanceId(code);
        instance.setFlag(0);
        instance.setResult(0);
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());

        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }

        LoginUser user = userService.getUserById(userId);

        if (Func.notNull(user)) {
            instance.setUpdateBy(user.getUsername());
        }
        instance.setUpdateTime(LocalDateTime.now());
        // 保存实例
        save(instance);

        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventType(TcEventType.TC_INSTANCE_CREATE);
        // 聚合数据
        EventContent content = new EventContent();
        content.setTitle(instanceVO.getTitle());
        content.setContent(instanceVO.getInstanceName());
        content.setUserCode(instanceVO.getFromUserId());

        eventEntity.setContext(Func.toJson(content));
        eventEntity.setCorpId(instanceVO.getCorpId());
        eventEntity.setAgentId(instanceVO.getAgentId());
        TcEvent event = new TcEvent(eventEntity);
        applicationContext.publishEvent(event);
        return instanceVO;
    }

    @Override
    public TodoInstanceDto findOneByInstance(TodoInstanceDto queryDto) {
        if (Func.isNull(queryDto)) {
            return null;
        }

        // 构造查询对象
        TodoInstance queryEntity = new TodoInstance();
        BeanUtil.copyProperties(queryDto, queryEntity);

        // 查询数据库
        TodoInstance entity = this.baseMapper.selectOneByInstance(queryEntity);
        if (Func.isNull(entity) || Func.isNull(entity.getId())) {
            return null;
        }

        // 构造结果 DTO 并返回
        TodoInstanceDto resultDto = new TodoInstanceDto();
        BeanUtil.copyProperties(entity, resultDto);
        return resultDto;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public PageResult<TodoInstanceUpdateDto> batchUpdateInstance(List<TodoInstanceUpdateDto> instanceUpdateVOS) {
        if (Func.isNull(instanceUpdateVOS)){
            return null;
        }

        List<TodoInstance> instances = new ArrayList<>();
        List<TodoTask> tasks = new ArrayList<>();
        List<TodoRead> reads = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<MsgInstance> msgInstanceVOS = new ArrayList<>();
        MsgInstance msgInstance;

        String corpId;
        String agentId;
        for (TodoInstanceUpdateDto instanceUpdateVO : instanceUpdateVOS){

            String instanceCode = instanceUpdateVO.getCode();
            corpId = instanceUpdateVO.getCorpId();
            agentId = instanceUpdateVO.getAgentId();

            TodoInstance instance = new TodoInstance();
            instance.setCode(instanceCode);
            instance.setAgentId(agentId);
            TodoInstance todoInstance = this.baseMapper.selectOneByInstance(instance);
            log.info(todoInstance.toString());

            // 判断实例是否存在
            if (Func.notNull(todoInstance)){
                // 创建需要更新的实例条件对象
                todoInstance.setFlag(instanceUpdateVO.getFlag());
                todoInstance.setResult(instanceUpdateVO.getResult());
                todoInstance.setUpdateTime(now);

                Integer flag = instanceUpdateVO.getFlag();
                // 审批通过通知
                if (Constants.FLAG_COMPLETE.equals(flag)){

                    // 消息体
                    msgInstance = new MsgInstance();
                    msgInstance.setMsgTitle(todoInstance.getTitle());
                    msgInstance.setTemplateId(Constants.BPM_INSTANCE_END);
                    msgInstance.setReceiveUserIdList(new String[]{todoInstance.getFromUserId()});
                    msgInstance.setPcUrl(todoInstance.getPcUrl());
                    msgInstance.setPhoneUrl(todoInstance.getH5Url());
                    msgInstance.setAgentId(instanceUpdateVO.getAgentId());
                    msgInstance.setCorpId(instanceUpdateVO.getCorpId());
                    Map<String, String> params = new HashMap<>();
                    params.put("finishon", CommonUtil.formatDateTime());
                    msgInstance.setTemplateParam(params);
                    msgInstanceVOS.add(msgInstance);

                    // 发送实例完成
                    EventContent content = new EventContent();
                    content.setTime(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                    content.setUserCode(todoInstance.getFromUserId());
                    content.setTitle(todoInstance.getTitle());
                    content.setContent("审批已完成");
                    content.setUrl(todoInstance.getPcUrl());

                    EventEntity eventEntity = new EventEntity();
                    eventEntity.setEventType(TcEventType.TC_INSTANCE_COMPLETE);
                    eventEntity.setContext(Func.toJson(content));
                    eventEntity.setCorpId(corpId);
                    eventEntity.setAgentId(agentId);
                    TcEvent event = new TcEvent(eventEntity);
                    applicationContext.publishEvent(event);
                }

                // 创建需要更新的待办条件对象
                List<TodoTaskDto> taskVOS = this.taskService.findByInstanceCode(instanceCode);
                if (Func.notNull(taskVOS) && !taskVOS.isEmpty()){

                    for (TodoTaskDto taskVO : taskVOS){

                        TodoTask task = new TodoTask();
//                        task.setId(taskVO.getId());
                        task.setInstanceCode(instanceCode);
                        task.setUpdateTime(now);
                        task.setFlag(instanceUpdateVO.getFlag());
                        task.setResult(instanceUpdateVO.getResult());
                        tasks.add(task);
                    }

                }
                // 创建需要更新的待阅条件对象
                List<TodoReadDto> readVOS = this.readService.findByInstanceCode(instanceCode);
                if (Func.notNull(readVOS) && !readVOS.isEmpty()){

                    for (TodoReadDto readVO : readVOS){

                        TodoRead read = new TodoRead();
                        read.setId(read.getId());
                        read.setInstanceCode(instanceCode);
                        read.setUpdateTime(now);
                        read.setFlag(instanceUpdateVO.getFlag());
                        reads.add(read);
                    }
                }
                instances.add(todoInstance);
            }
        }
        // 更新实例对象状态
        updateBatchById(instances);

        this.taskService.updateBatchById(tasks);
        this.readService.updateBatchById(reads);
        // 发送审批通过通知
        if (!msgInstanceVOS.isEmpty()){
            for (MsgInstance instanceVO : msgInstanceVOS){
//                msgService.sendMessage(instanceVO);
                // TODO 此处与 信息化中心对接
            }
        }
        PageResult<TodoInstanceUpdateDto> result = new PageResult<>();
        result.setPageNo(1);
        result.setPageSize(instanceUpdateVOS.size());
        result.setTotal(instanceUpdateVOS.size());
        result.setData(instanceUpdateVOS);
        return result;
    }

    @Override
    public PageResult<TodoInstanceDto> findByUserIdPage(BaseTcDto baseTcDto) {
        CommonUtil.checkPageAndOrderParam(baseTcDto);
        IPage<TodoInstance> page = new Page<>(baseTcDto.getPageNo(), baseTcDto.getPageSize());

        QueryWrapper<TodoInstance> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(baseTcDto.getUserCode())) {
            queryWrapper.eq("from_user_id", baseTcDto.getUserCode());
        }
        if (Func.isNotEmpty(baseTcDto.getTypeCode())){
            queryWrapper.eq("instance_type", baseTcDto.getTypeCode());
        }
        if (Func.isNotEmpty(baseTcDto.getFlag())){
            queryWrapper.eq("flag", baseTcDto.getFlag());
        }
        CommonUtil.pageQueryWrapper(queryWrapper, baseTcDto);

        // 执行分页查询
        IPage<TodoInstance> iPage = this.baseMapper.selectPage(page, queryWrapper);
        List<TodoInstance> records = iPage.getRecords();

        // 封装返回 DTO 列表
        List<TodoInstanceDto> dtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(records)) {
            for (TodoInstance ins : records) {
                TodoInstanceDto dto = new TodoInstanceDto();
                BeanUtil.copyProperties(ins, dto);
                dto.setPcUrl(StringEscapeUtils.unescapeXml(ins.getPcUrl()));
                dto.setH5Url(StringEscapeUtils.unescapeXml(ins.getH5Url()));
                dtoList.add(dto);
            }
        }

        PageResult<TodoInstanceDto> result = CommonUtil.buildPageResult(iPage, dtoList);
        return result;

    }


}
