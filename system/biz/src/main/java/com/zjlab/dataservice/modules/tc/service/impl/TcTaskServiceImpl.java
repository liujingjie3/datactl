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
import com.zjlab.dataservice.modules.tc.event.*;
import com.zjlab.dataservice.modules.tc.mapper.TodoTaskMapper;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.entity.MsgInstance;
import com.zjlab.dataservice.modules.tc.model.entity.TodoTask;
import com.zjlab.dataservice.modules.tc.service.TcInstanceService;
import com.zjlab.dataservice.modules.tc.service.TcTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcTaskServiceImpl extends ServiceImpl<TodoTaskMapper, TodoTask> implements
        TcTaskService {

    @Autowired
    private TcApplicationListener applicationContext;

    @Autowired
    private ISysBaseAPI userService;

    @Lazy
    @Autowired
    private TcInstanceService instanceService;

    @Override
    public PageResult<TodoTaskDto> selectTodoTaskPage(TodoTaskQueryDto todoTaskQueryDto) {
        // 参数校验
        CommonUtil.checkPageAndOrderParam(todoTaskQueryDto);
        IPage<TodoTaskQueryDto> page = new Page<>(todoTaskQueryDto.getPageNo(), todoTaskQueryDto.getPageSize());

        // 查询数据
        IPage<TodoTaskDto> resultPage = baseMapper.selectTodoTaskPage(page, todoTaskQueryDto);

        List<TodoTaskDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TodoTaskDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;
    }


    /**
     * 批量创建待办信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<TodoTaskDto> createTasks(List<TodoTaskDto> taskVOS) {
        if (Func.isNull(taskVOS) || taskVOS.isEmpty()){
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        List<TodoTask> tasks = new ArrayList<>();

        List<MsgInstance> msgInstanceVOS = new ArrayList<>();
        MsgInstance msgInstanceVO;

        for (TodoTaskDto taskVO : taskVOS){

            TodoInstanceDto instanceVO = new TodoInstanceDto();
            instanceVO.setAgentId(taskVO.getAgentId());
            instanceVO.setCode(taskVO.getInstanceCode());
            instanceVO = this.instanceService.findOneByInstance(instanceVO);
            if (Func.isNull(instanceVO) || Func.isNull(instanceVO.getCode())){
                throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
            }

            // 通过实例编码和待办任务编码查询关联记录,存在跳过
            TodoTask todoTask = this.baseMapper.selectOneByRequest(taskVO);
            if (Func.notNull(todoTask)){
                throw new BaseException(ResultCode.TASK_IS_EXISTS);
            }

            TodoTask task = new TodoTask();
            BeanUtil.copyProperties(taskVO, task);
            task.setInstanceId(instanceVO.getInstanceId());
            task.setTaskId(Func.randomUUID());
            taskVO.setTaskId(task.getTaskId());
            task.setFlag(0);
            task.setResult(0);
            task.setCreateTime(now);

            if (Func.isNotBlank(taskVO.getPcUrl())) {
                task.setPcUrl(StringEscapeUtils.unescapeXml(taskVO.getPcUrl()));
            }
            if (Func.isNotBlank(taskVO.getH5Url())) {
                task.setH5Url(StringEscapeUtils.unescapeXml(taskVO.getH5Url()));
            }

            String userId = UserThreadLocal.getUserId();
            if (userId == null) {
                throw new BaseException(ResultCode.USERID_IS_NULL);
            }
            LoginUser user = userService.getUserById(userId);

            if (Func.notNull(user)) {
                task.setCreateBy(user.getUsername());
                task.setUpdateBy(user.getUsername());
            }
            task.setUpdateTime(now);
            tasks.add(task);

            Long count = countByInstanceCode(instanceVO.getCode());
//             流程第一步任务发送给自己不发通知
            if (count == 0 && instanceVO.getFromUserId().equals(task.getToUserId())) {

            } else {
                // 发送待办事件
                EventEntity eventEntity = new EventEntity();
                eventEntity.setEventType(TcEventType.TC_TASK_CREATE);
                // 聚合数据
                EventContent content = new EventContent();
                content.setTitle(instanceVO.getTitle());
                content.setContent("需要您审批");
                content.setUrl(task.getPcUrl());
                content.setUserCode(task.getToUserId());
                content.setTime(System.currentTimeMillis());

                eventEntity.setContext(Func.toJson(content));
                eventEntity.setCorpId(instanceVO.getCorpId());
                eventEntity.setAgentId(instanceVO.getAgentId());
                TcEvent event = new TcEvent(eventEntity);
                applicationContext.publishEvent(event);

                // 消息体
                msgInstanceVO = new MsgInstance();
                msgInstanceVO.setMsgTitle(instanceVO.getTitle());
                msgInstanceVO.setTemplateId(Constants.BPM_TASK);
                msgInstanceVO.setReceiveUserIdList(new String[]{task.getToUserId()});
                msgInstanceVO.setPcUrl(task.getPcUrl());
                msgInstanceVO.setPhoneUrl(task.getH5Url());
                msgInstanceVO.setAgentId(taskVO.getAgentId());
                msgInstanceVO.setCorpId(taskVO.getCorpId());
                Map<String, String> params = new HashMap<>();
//                if (Func.isNotEmpty(instanceVO.getFromUserId())) {
//                    if (Func.notNull(userR.getData())) {
//                        UserDTO userInfo = userR.getData();
//                        params.put("from_username", userInfo.getName());
//                        String deptId = userInfo.getDeptId();
//                        if (Func.isNumeric(deptId)) {
//
//                            R<DeptDTO> deptDTOR = this.deptClient.getDept(Long.parseLong(deptId));
//                            if (Func.notNull(deptDTOR.getData())) {
//                                params.put("deptname", deptDTOR.getData().getDeptName());
//                            }
//                        }
//                    }
//                    msgInstanceVO.setTemplateParam(params);
//                }

                msgInstanceVOS.add(msgInstanceVO);
            }
        }
        saveBatch(tasks);
        // 发送待办提醒
        if (!msgInstanceVOS.isEmpty()){
            for (MsgInstance instanceVO : msgInstanceVOS){
//                msgService.sendMessage(instanceVO);
            }
        }
        return taskVOS;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public TodoTaskUpdateDto batchUpdateTasks(TodoTaskUpdateDto taskUpdateDto) {
        if (Func.isNull(taskUpdateDto)){
            return null;
        }
        TodoInstanceDto instanceDto = new TodoInstanceDto();
        instanceDto.setAgentId(taskUpdateDto.getAgentId());
        instanceDto.setCode(taskUpdateDto.getInstanceCode());
        instanceDto = this.instanceService.findOneByInstance(instanceDto);
        if (Func.isNull(instanceDto) || Func.isNull(instanceDto.getCode())){
            throw new BaseException(ResultCode.INSTANCE_IS_NOT_EXISTS);
        }
        /**
         * 发起人工号
         */
        String fromUserId = instanceDto.getFromUserId();
        LocalDateTime now = LocalDateTime.now();
        List<TodoTask> tasks = new ArrayList<>();
        List<BaseTaskDto> baseVOS = taskUpdateDto.getTasks();

        List<MsgInstance> msgInstanceVOS = new ArrayList<>();
        MsgInstance msgInstanceVO;
        if (Func.notNull(baseVOS) && !baseVOS.isEmpty()) {

            for (BaseTaskDto baseTaskDto : baseVOS) {
                // 通过实例编码和待办任务编码查询关联记录
                QueryWrapper<TodoTask> queryWrapper = new QueryWrapper<>();
                if (Func.isNotBlank(taskUpdateDto.getAgentId())) {
                    queryWrapper.eq("agent_id", taskUpdateDto.getAgentId());
                }
                if (Func.isNotBlank(taskUpdateDto.getInstanceCode())) {
                    queryWrapper.eq("instance_code", taskUpdateDto.getInstanceCode());
                }
                if (Func.isNotBlank(baseTaskDto.getTaskCode())) {
                    queryWrapper.eq("code", baseTaskDto.getTaskCode());
                }
                if (Func.isNotBlank(baseTaskDto.getTaskId())){
                    queryWrapper.eq("task_id", baseTaskDto.getTaskId());
                }
                queryWrapper.last("limit 1");
                TodoTask todoTask = this.baseMapper.selectOne(queryWrapper);
                if (Func.notNull(todoTask)) {

                    String userId = UserThreadLocal.getUserId();
                    if (userId == null) {
                        throw new BaseException(ResultCode.USERID_IS_NULL);
                    }
                    LoginUser user = userService.getUserById(userId);

                    if (Func.notNull(user)) {
                        todoTask.setUpdateBy(user.getUsername());
                        todoTask.setGmtAuditBy(user.getUsername());
                    }
                    todoTask.setUpdateTime(now);
                    todoTask.setGmtAudit(now);
                    BeanUtil.copyProperties(baseTaskDto, todoTask);
                    tasks.add(todoTask);

                } else {
//                    log.error("task query params: {}", queryWrapper.toString());
                    throw new BaseException(ResultCode.TASK_IS_NOT_EXISTS);
                }
                /**
                 * 审批人工号
                 */
                String toUserId = todoTask.getToUserId();
                /**
                 * 审批人和发起人相同不推送消息
                 */
                if (fromUserId.equals(toUserId)) {

                } else {
                    if (Constants.FLAG_COMPLETE.equals(todoTask.getFlag())) {
                        // 条件判断同意还是拒绝
                        String result = Constants.FLAG_COMPLETE.equals(todoTask.getResult())
                                ? Constants.BPM_AUDIT_SUCCESS : Constants.BPM_AUDIT_BACK;
                        // 消息体
                        msgInstanceVO = new MsgInstance();
                        msgInstanceVO.setMsgTitle(instanceDto.getTitle());
                        msgInstanceVO.setTemplateId(result);
                        msgInstanceVO.setReceiveUserIdList(new String[]{instanceDto.getFromUserId()});
                        msgInstanceVO.setPcUrl(todoTask.getPcUrl());
                        msgInstanceVO.setPhoneUrl(todoTask.getH5Url());
                        msgInstanceVO.setAgentId(taskUpdateDto.getAgentId());
                        msgInstanceVO.setCorpId(taskUpdateDto.getCorpId());
                        Map<String, String> params = new HashMap<>();
                        // TODO 节点审批完成发送消息
//                        if (Func.isNotEmpty(instanceVO.getFromUserId())) {
//
//                            R<UserDTO> userR = this.userClient.getUserByCode(todoTask.getToUserId());
//                            if (Func.notNull(userR.getData())) {
//                                UserDTO userInfo = userR.getData();
//                                params.put("to_username", userInfo.getName());
//                                params.put("auditon", DateTimeUtil.formatDateTime(now));
//                                params.put("opinion", todoTask.getOpinion());
//                            }
//                            msgInstanceVO.setTemplateParam(params);
//                        }

                        msgInstanceVOS.add(msgInstanceVO);
                    }

                    // 发送实例完成
                    EventContent content = new EventContent();
                    content.setTime(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                    content.setUserCode(instanceDto.getFromUserId());
                    content.setTitle(instanceDto.getTitle());
                    content.setUrl(instanceDto.getPcUrl());
                    content.setContent(Constants.FLAG_COMPLETE.equals(todoTask.getResult())
                            ? "审批已通过" : "需要您审批");
                    EventEntity eventEntity = new EventEntity();
                    eventEntity.setEventType(Constants.FLAG_COMPLETE.equals(todoTask.getResult())
                            ? TcEventType.TC_TASK_AGREE : TcEventType.TC_TASK_REFUSED);
                    eventEntity.setContext(Func.toJson(content));
                    TcEvent event = new TcEvent(eventEntity);
                    applicationContext.publishEvent(event);
                }
            }
            // 批量更新待办任务
            updateBatchById(tasks);
        }

        // 发送待办审批通过通知
        if (!msgInstanceVOS.isEmpty()){
            for (MsgInstance msg : msgInstanceVOS){
//                msgService.sendMessage(msg);
            }
        }
        return taskUpdateDto;
    }

    @Override
    public PageResult<TodoTaskDto> findByUserIdPage(BaseTcDto baseTcDto) {

        // 分页参数构造
        IPage<TodoTask> todoTaskPage = new Page<>(baseTcDto.getPageNo(), baseTcDto.getPageSize());

        // 查询条件封装
        QueryWrapper<TodoTask> queryWrapper = new QueryWrapper<>();
        if (Func.notNull(baseTcDto.getUserCode())) {
            queryWrapper.eq("to_user_id", baseTcDto.getUserCode());
        }
        CommonUtil.pageQueryWrapper(queryWrapper, baseTcDto);

        // 执行分页查询
        IPage<TodoTask> iPage = this.baseMapper.selectPage(todoTaskPage, queryWrapper);
        List<TodoTask> records = iPage.getRecords();

        // 封装返回 DTO 列表
        List<TodoTaskDto> dtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(records)) {
            for (TodoTask task : records) {
                TodoTaskDto dto = new TodoTaskDto();
                BeanUtil.copyProperties(task, dto);
                dto.setPcUrl(StringEscapeUtils.unescapeXml(task.getPcUrl()));
                dto.setH5Url(StringEscapeUtils.unescapeXml(task.getH5Url()));
                dtoList.add(dto);
            }
        }

        PageResult<TodoTaskDto> result = CommonUtil.buildPageResult(iPage, dtoList);
        return result;
    }

    @Override
    public PageResult<TaskWebDto> findTaskInstancePage(BaseTcDto baseTcDto) {
        // 参数检查及默认处理
        CommonUtil.checkPageAndOrderParam(baseTcDto);

        // 分页查询
        IPage<TaskWebDto> page = new Page<>(baseTcDto.getPageNo(), baseTcDto.getPageSize());
        IPage<TaskWebDto> resultPage = baseMapper.selectTaskInstancePage(page, baseTcDto);

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
    public boolean updateBatchInstanceCode(List<TodoTask> list) {
        if (Func.isNull(list) || list.isEmpty()){
            return false;
        }

        int update = this.baseMapper.updateBatchByInstanceCode(list);
        return CommonUtil.isUpdateSuccess(update);
    }

    @Override
    public List<TodoTaskDto> getList(TodoTaskQueryDto queryDto) {
        if (queryDto == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<TodoTask> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .eq(StringUtils.isNotBlank(queryDto.getInstanceCode()), TodoTask::getInstanceCode, queryDto.getInstanceCode())
                .eq(StringUtils.isNotBlank(queryDto.getTaskCode()), TodoTask::getCode, queryDto.getTaskCode())
                .eq(StringUtils.isNotBlank(queryDto.getTaskId()), TodoTask::getTaskId, queryDto.getTaskId())
                .eq(Objects.nonNull(queryDto.getFlag()), TodoTask::getFlag, queryDto.getFlag());

        List<TodoTask> tasks = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        return tasks.stream()
                .map(task -> {
                    TodoTaskDto dto = new TodoTaskDto();
                    BeanUtil.copyProperties(task, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoTaskDto> findByInstanceCode(@NotNull String instanceCode) {
        if (StringUtils.isBlank(instanceCode)) {
            return Collections.emptyList();
        }
        List<TodoTaskDto> tasks = baseMapper.selectTodoTaskByflow(instanceCode);
        // 遍历打印每条记录
        System.out.println("tasks : ");
        tasks.forEach(task -> System.out.println(task));
        return tasks;
    }

    @Override
    public TodoTaskDto findOneByCodeTaskId(String taskCode) {
        if (Func.isAllEmpty(taskCode)) {
            return null;
        }

        LambdaQueryWrapper<TodoTask> queryWrapper = Wrappers.lambdaQuery();
        if (Func.isNotBlank(taskCode)) {
            queryWrapper.eq(TodoTask::getCode, taskCode);
        }

        queryWrapper.eq(TodoTask::getFlag, 0)
                .last("limit 1");

        TodoTask task = this.baseMapper.selectOne(queryWrapper);
        if (task == null) {
            return null;
        }

        return BeanUtil.copyProperties(task, TodoTaskDto.class);
    }

    @Override
    public Long findByUserCount(BaseTcDto tcDto) {
        return baseMapper.selectByUserCount(tcDto);
    }

    @Override
    public Long countByInstanceCode(String instanceCode) {
        if (Func.isBlank(instanceCode)) {
            return null;
        }
        return this.baseMapper.countByInstanceCode(instanceCode);
    }

    @Override
    public PageResult<TaskRemindDto> getRemindTasks(TaskRemindDto remindDto) {
        CommonUtil.checkPageAndOrderParam(remindDto);
        IPage<TaskRemindDto> page = new Page<>(remindDto.getPageNo(), remindDto.getPageSize());
        IPage<TaskRemindDto> resultPage = baseMapper.selectCountByUserPage(page, remindDto);
        List<TaskRemindDto> listRecords = resultPage.getRecords();

        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TaskRemindDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;

    }

}
