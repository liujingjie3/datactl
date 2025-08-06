package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.zjlab.dataservice.modules.tc.event.TcEvent;
import com.zjlab.dataservice.modules.tc.event.TcEventType;
import com.zjlab.dataservice.modules.tc.mapper.TodoReadMapper;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.TodoRead;
import com.zjlab.dataservice.modules.tc.service.TcInstanceService;
import com.zjlab.dataservice.modules.tc.service.TcReadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjlab.dataservice.modules.tc.event.EventEntity;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcReadServiceImpl extends ServiceImpl<TodoReadMapper, TodoRead> implements
        TcReadService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ISysBaseAPI userService;

    @Lazy
    @Autowired
    private TcInstanceService instanceService;

    @Override
    public PageResult<TodoReadDto> selectTodoReadPage(TodoReadDto todoReadDto) {
        // 参数校验
        CommonUtil.checkPageAndOrderParam(todoReadDto);
        IPage<TodoReadDto> page = new Page<>(todoReadDto.getPageNo(), todoReadDto.getPageSize());

        // 查询数据
        IPage<TodoReadDto> resultPage = baseMapper.selectTodoReadPage(page, todoReadDto);

        List<TodoReadDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TodoReadDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;
    }

    /**
     * 批量创建待阅信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<TodoReadDto> createReads(List<TodoReadDto> readVOS) {
        if (Func.isNull(readVOS) || readVOS.isEmpty()){
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        List<TodoRead> reads = new ArrayList<>();

        for (TodoReadDto readVO : readVOS){

            TodoInstanceDto instanceVO = new TodoInstanceDto();
            instanceVO.setAgentId(readVO.getAgentId());
            instanceVO.setCode(readVO.getInstanceCode());
            instanceVO = this.instanceService.findOneByInstance(instanceVO);
            if (Func.isNull(instanceVO) || Func.isNull(instanceVO.getCode())){
                throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
            }

            // 通过实例编码和待阅任务编码查询关联记录,存在跳过
            TodoRead todoRead = baseMapper.selectOneByRequest(readVO);
            if (Func.notNull(todoRead)){
                throw new BaseException(ResultCode.TASK_IS_EXISTS);
            }

            TodoRead read = new TodoRead();
            BeanUtil.copyProperties(readVO, read);
            read.setInstanceId(instanceVO.getInstanceId());
            read.setReadId(Func.randomUUID());
            readVO.setReadId(read.getReadId());
            read.setFlag(0);
            read.setCreateTime(now);

            if (Func.isNotBlank(readVO.getPcUrl())) {
                read.setPcUrl(StringEscapeUtils.unescapeXml(readVO.getPcUrl()));
            }
            if (Func.isNotBlank(readVO.getH5Url())) {
                read.setH5Url(StringEscapeUtils.unescapeXml(readVO.getH5Url()));
            }

            String userId = UserThreadLocal.getUserId();
            if (userId == null) {
                throw new BaseException(ResultCode.USERID_IS_NULL);
            }
            LoginUser user = userService.getUserById(userId);

            if (Func.notNull(user)) {
                read.setCreateBy(user.getUsername());
                read.setUpdateBy(user.getUsername());
            }
            read.setUpdateTime(now);
            reads.add(read);

            EventEntity taskEntity = new EventEntity();
            taskEntity.setEventType(TcEventType.TC_READ_CREATE);
            taskEntity.setContext(read);
            taskEntity.setCorpId(readVO.getCorpId());
            taskEntity.setAgentId(readVO.getAgentId());
            TcEvent taskEvent = new TcEvent(taskEntity);
            applicationContext.publishEvent(taskEvent);
//
//            // 消息体
//            msgInstanceVO = new MsgInstanceVO();
//            msgInstanceVO.setMsgTitle(instanceVO.getTitle());
//            msgInstanceVO.setTemplateId(Constants.BPM_READ);
//            msgInstanceVO.setReceiveUserIdList(new String[]{read.getToUserId()});
//            msgInstanceVO.setPcUrl(read.getPcUrl());
//            msgInstanceVO.setPhoneUrl(read.getH5Url());
//            msgInstanceVO.setAgentId(readVO.getAgentId());
//            msgInstanceVO.setCorpId(readVO.getCorpId());
//            Map<String, String> params = new HashMap<>();
//            if (Func.isNotEmpty(instanceVO.getFromUserId())){
//
//                R<UserDTO> userR = this.userClient.getUserByCode(instanceVO.getFromUserId());
//                if (Func.notNull(userR.getData())) {
//                    UserDTO userInfo = userR.getData();
//                    params.put("from_username", userInfo.getName());
//                    String deptId = userInfo.getDeptId();
//                    if (Func.isNumeric(deptId)){
//
//                        R<DeptDTO> deptDTOR = this.deptClient.getDept(Long.parseLong(deptId));
//                        if (Func.notNull(deptDTOR.getData())) {
//                            log.info("deptName: [{}]", deptDTOR.getData().getDeptName());
//                            params.put("deptname", deptDTOR.getData().getDeptName());
//                        }
//                    }
//                }
//                msgInstanceVO.setTemplateParam(params);
//            }
//
//                msgInstanceVOS.add(msgInstanceVO);
//            }
        }
        saveBatch(reads);
        // 发送待阅提醒
//        log.info("send message: {}", msgInstanceVOS);
//        if (!msgInstanceVOS.isEmpty()){
//            callBackService.execute(() -> {
//
//                for (MsgInstanceVO instanceVO : msgInstanceVOS){
//                    sendMsgClient.sendMessage(instanceVO);
//                }
//            });
//        }

        return readVOS;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public TodoReadUpdateDto batchUpdateReads(TodoReadUpdateDto readUpdateDto) {
        if (Func.isNull(readUpdateDto)){
            return null;
        }
        TodoInstanceDto instanceDto = new TodoInstanceDto();
        instanceDto.setAgentId(readUpdateDto.getAgentId());
        instanceDto.setCode(readUpdateDto.getInstanceCode());
        instanceDto = this.instanceService.findOneByInstance(instanceDto);

        if (Func.isNull(instanceDto) || Func.isNull(instanceDto.getCode())){
            throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        List<TodoRead> reads = new ArrayList<>();
        List<BaseReadDto> baseVOS = readUpdateDto.getReads();

        if (Func.notNull(baseVOS) && !baseVOS.isEmpty()) {
            for (BaseReadDto baseTaskDto : baseVOS) {
                // 通过实例编码和待阅任务编码查询关联记录
                QueryWrapper<TodoRead> queryWrapper = new QueryWrapper<>();
                if (Func.isNotBlank(readUpdateDto.getAgentId())) {
                    queryWrapper.eq("agent_id", readUpdateDto.getAgentId());
                }
                if (Func.isNotBlank(readUpdateDto.getInstanceCode())) {
                    queryWrapper.eq("instance_code", readUpdateDto.getInstanceCode());
                }
                if (Func.isNotBlank(baseTaskDto.getReadCode())) {
                    queryWrapper.eq("code", baseTaskDto.getReadCode());
                }
                if (Func.isNotBlank(baseTaskDto.getReadId())){
                    queryWrapper.eq("read_id", baseTaskDto.getReadId());
                }
                queryWrapper.last("limit 1");
                TodoRead todoRead = this.baseMapper.selectOne(queryWrapper);
                if (Func.notNull(todoRead)) {

                    String userId = UserThreadLocal.getUserId();
                    if (userId == null) {
                        throw new BaseException(ResultCode.USERID_IS_NULL);
                    }
                    LoginUser user = userService.getUserById(userId);

                    if (Func.notNull(user)) {
                        todoRead.setUpdateBy(user.getUsername());
                        todoRead.setGmtReadBy(user.getUsername());
                    }
                    todoRead.setUpdateTime(now);
                    todoRead.setGmtRead(now);
                    BeanUtil.copyProperties(baseTaskDto, todoRead);
                    reads.add(todoRead);

                    // 加入任务事件
                    EventEntity taskEntity = new EventEntity();
                    taskEntity.setEventType(TcEventType.TC_READ_UPDATE_STATUS);
                    taskEntity.setContext(todoRead);
                    taskEntity.setCorpId(instanceDto.getCorpId());
                    taskEntity.setAgentId(instanceDto.getAgentId());
                    TcEvent taskEvent = new TcEvent(taskEntity);
                    applicationContext.publishEvent(taskEvent);

                } else {
                    throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
                }
            }
            // 批量更新待办任务
            updateBatchById(reads);
        }

        return readUpdateDto;
    }

    @Override
    public PageResult<TodoReadDto> findByUserIdPage(BaseTcDto baseTcDto) {

        IPage<TodoRead> todoReadPage = new Page<>(baseTcDto.getPageNo(), baseTcDto.getPageSize());

        // 查询条件封装
        QueryWrapper<TodoRead> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(baseTcDto.getUserCode())) {
            queryWrapper.eq("to_user_id", baseTcDto.getUserCode());
        }
        CommonUtil.pageQueryWrapper(queryWrapper, baseTcDto);

        // 执行分页查询
        IPage<TodoRead> iPage = this.baseMapper.selectPage(todoReadPage, queryWrapper);
        List<TodoRead> records = iPage.getRecords();

        // 封装返回 DTO 列表
        List<TodoReadDto> dtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(records)) {
            for (TodoRead read : records) {
                TodoReadDto dto = new TodoReadDto();
                BeanUtil.copyProperties(read, dto);
                dto.setPcUrl(StringEscapeUtils.unescapeXml(read.getPcUrl()));
                dto.setH5Url(StringEscapeUtils.unescapeXml(read.getH5Url()));
                dtoList.add(dto);
            }
        }

        PageResult<TodoReadDto> result = CommonUtil.buildPageResult(iPage, dtoList);
        return result;
    }

    @Override
    public PageResult<TaskWebDto> findReadInstancePage(BaseTcDto baseTcDto) {
        // 参数检查及默认处理
        CommonUtil.checkPageAndOrderParam(baseTcDto);

        // 分页查询
        IPage<TaskWebDto> page = new Page<>(baseTcDto.getPageNo(), baseTcDto.getPageSize());
        IPage<TaskWebDto> resultPage = baseMapper.selectReadInstancePage(page, baseTcDto);

        List<TaskWebDto> records = resultPage.getRecords();

        // 若无数据则返回空分页结果
        if (CollectionUtils.isEmpty(records)) {
            return new PageResult<>();
        }

        // XML 解码处理
        records.forEach(item -> {
            item.setPcUrl(StringEscapeUtils.unescapeXml(item.getPcUrl()));
            item.setH5Url(StringEscapeUtils.unescapeXml(item.getH5Url()));
        });

        // 返回封装分页结果
        return CommonUtil.buildPageResult(resultPage, records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchInstanceCode(List<TodoRead> list) {
        if (Func.isNull(list) || list.isEmpty()){
            return false;
        }

        int update = this.baseMapper.updateBatchByInstanceCode(list);
        return CommonUtil.isUpdateSuccess(update);
    }

    @Override
    public List<TodoReadDto> findByInstanceCode(@NotNull String instanceCode) {
        if (StringUtils.isBlank(instanceCode)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<TodoRead> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TodoRead::getInstanceCode, instanceCode)
                .eq(TodoRead::getFlag, Constants.FLAG_RUNNING);

        List<TodoRead> tasks = this.baseMapper.selectList(queryWrapper);

        // 使用流方式转换为 DTO
        return tasks.stream()
                .map(task -> {
                    TodoReadDto dto = new TodoReadDto();
                    BeanUtil.copyProperties(task, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }



}
