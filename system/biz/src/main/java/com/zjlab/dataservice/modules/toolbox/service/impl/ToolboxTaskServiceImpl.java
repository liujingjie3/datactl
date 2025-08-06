package com.zjlab.dataservice.modules.toolbox.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.RestUtil;
import com.zjlab.dataservice.common.util.UUIDGenerator;
import com.zjlab.dataservice.modules.toolbox.dto.TaskDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.enumerate.TaskLogType;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import com.zjlab.dataservice.modules.toolbox.mapper.ToolBoxTaskMapper;
import com.zjlab.dataservice.modules.toolbox.po.FlowTemplatePo;
import com.zjlab.dataservice.modules.toolbox.po.TaskLogPo;
import com.zjlab.dataservice.modules.toolbox.po.TaskPo;
import com.zjlab.dataservice.modules.toolbox.service.FlowTemplateService;
import com.zjlab.dataservice.modules.toolbox.service.TaskLogService;
import com.zjlab.dataservice.modules.toolbox.service.ToolboxTaskService;
import com.zjlab.dataservice.modules.toolbox.vo.TaskVo;
//import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ToolboxTaskServiceImpl extends ServiceImpl<ToolBoxTaskMapper, TaskPo> implements ToolboxTaskService {

    @Resource
    private TaskLogService taskLogService;

    @Resource
    private FlowTemplateService flowTemplateService;

    @Override
    public PageResult<TaskVo> listTaskByCondition(ToolBoxDto toolBoxDto) {
        PageResult<TaskVo> result = new PageResult<>();
        String userId = checkUserIdExist();
        log.info("[ToolboxTaskService listTaskByCondition]" + Optional.ofNullable(toolBoxDto).orElse(new ToolBoxDto()));

        if (toolBoxDto == null) {
            log.info("[ToolboxTaskService listTaskByCondition]toolBoxDto is null, use default value");
            toolBoxDto = new ToolBoxDto();
            toolBoxDto.setPageNo(1);
            toolBoxDto.setPageSize(10);
            toolBoxDto.setOrderByField("updateTime");
        }

        IPage<TaskPo> page = new Page<>(Optional.ofNullable(toolBoxDto.getPageNo()).orElse(1), Optional.ofNullable(toolBoxDto.getPageSize()).orElse(10));
        LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<>();
        query.eq(TaskPo::getUserId, userId);
        ToolBoxStatus status = ToolBoxStatus.fromString(toolBoxDto.getStatus());
        if (status != null) {
            if (status != ToolBoxStatus.READY) {
                query.eq(TaskPo::getStatus, status);
            } else {
                query.in(TaskPo::getStatus, Arrays.asList(ToolBoxStatus.INIT, ToolBoxStatus.EDITING, ToolBoxStatus.READY));
            }
        }
        if (StringUtil.isNotBlank(toolBoxDto.getTaskName())) {
            query.like(TaskPo::getTaskName, toolBoxDto.getTaskName());
        }
        query.notIn(TaskPo::getStatus, ToolBoxStatus.DELETE);
        query.orderByDesc(TaskPo::getUpdateTime);
        IPage<TaskPo> taskListByUser = baseMapper.selectPage(page, query);

        result.setPageNo((int) taskListByUser.getCurrent());
        result.setPageSize((int) taskListByUser.getSize());
        result.setTotal((int) taskListByUser.getTotal());
        List<TaskPo> taskPoList = taskListByUser.getRecords();
        result.setData(taskPoList.stream().map(this::po2vo).collect(Collectors.toList()));

        return result;
    }

    @Override
    public TaskVo queryTask(String taskId) {
        TaskVo result = new TaskVo();
        LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<>();
        query.eq(TaskPo::getTaskId, taskId);
        TaskPo taskPo = baseMapper.selectOne(query);
        if (taskPo != null) {
            BeanUtils.copyProperties(taskPo, result);
        } else {
            return null;
        }

        // 任务进行时的线条更新 todo: 未来更新逻辑
        if (Arrays.asList(ToolBoxStatus.RUNNING).contains(taskPo.getStatus())) {
            String taskConfiguration = taskPo.getTaskConfiguration();
            JSONObject tskConObject = JSONObject.parseObject(taskConfiguration);
            List<JSONObject> edges = tskConObject.getObject("edges", List.class);
            List<JSONObject> nodes = tskConObject.getObject("nodes", List.class);

            boolean statusOne = false;
            boolean statusTwo = false;

            for (JSONObject node : nodes) {
                JSONObject properties = (JSONObject) node.get("properties");
                if ("1".equals(node.get("id"))) {
                    statusOne = "SUCCESS".equals(properties.get("runState"));
                }
                if ("2".equals(node.get("id"))) {
                    statusTwo = "SUCCESS".equals(properties.get("runState"));
                }
            }

            if (statusOne && statusTwo) {
                result.setRunningLine("polyline-3");
            } else if (statusOne && (!statusTwo)) {
                result.setRunningLine("polyline-2");
            } else if ((!statusOne) && (!statusTwo)) {
                result.setRunningLine("polyline-1");
            }
        }


        // 仅三种任务状态下需要更新log信息，防止快速任务的结果被提前知道
        if (Arrays.asList(ToolBoxStatus.RUNNING, ToolBoxStatus.SUCCESS, ToolBoxStatus.FAIL)
                .contains(taskPo.getStatus())) {
            log.info("[ToolboxTaskService-updateLog] start!");
            String taskConfiguration = taskPo.getTaskConfiguration();
            // 加入log字段，但不重复查询
            Map<String, String> taskLog = getTaskLog(taskId, taskPo.getData());

            JSONObject tskConObject = JSONObject.parseObject(taskConfiguration);
            List<JSONObject> nodes = tskConObject.getObject("nodes", List.class);
            taskLog.forEach((key, value) -> {
                        for (JSONObject node : nodes) {
                            if (key.equals(node.get("id"))) {
                                JSONObject properties = (JSONObject) node.get("properties");
                                properties.put("log", value);
                            }
                        }
                    }
            );

            tskConObject.put("nodes", nodes);
//        LambdaQueryWrapper<TaskPo> updateQuery = new LambdaQueryWrapper<>();
//        updateQuery.eq(TaskPo::getTaskId, taskId);
//
//        int update = baseMapper.update(taskPo, updateQuery);
            baseMapper.updateLog(taskId, tskConObject.toString());
            result.setTaskConfiguration(tskConObject.toString());
        }
        // 仅在任务成功时获取结果数据
        if (ToolBoxStatus.SUCCESS.equals(taskPo.getStatus())) {
            List<JSONObject> tskResList = new ArrayList<>();
            String taskResult = taskPo.getTaskResult();
            JSONObject tskResObject = JSONObject.parseObject(taskResult);

            for (String key : tskResObject.keySet()) {
                JSONObject newObj = new JSONObject();
                newObj.put("file_id", key);
                JSONObject v = tskResObject.getJSONObject(key);
                newObj.putAll(v);
                tskResList.add(newObj);
            }
            result.setTaskResult(tskResList);
        }
        return result;
    }

    @Override
    public TaskVo createTask(TaskDto taskDto) {
        TaskVo result = new TaskVo();
        String userId = checkUserIdExist();

        // 检查是否已存在taskName，如果存在则创建失败
        String taskName = taskDto.getTaskName();
        if (StringUtil.isNotBlank(taskName)) {
            LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<>();
            query.eq(TaskPo::getTaskName, taskName);
            // 排除已删除的条目
            query.notIn(TaskPo::getStatus, ToolBoxStatus.DELETE);
            TaskPo taskPo = baseMapper.selectOne(query);
            if (taskPo != null) {
                return result;
            } else {
                taskPo = new TaskPo();
                BeanUtils.copyProperties(taskDto, taskPo);
                String uuid = UUIDGenerator.generate();
                taskPo.setTaskId(uuid);
                taskPo.setUserId(userId);
                taskPo.setStatus(ToolBoxStatus.INIT);
                taskPo.setCreateTime(new Date());
                taskPo.setUpdateTime(taskPo.getCreateTime());
                baseMapper.insert(taskPo);
                BeanUtils.copyProperties(taskPo, result);
            }
        }
        return result;
    }

    @Override
    public boolean deleteTaskById(String taskId) {
        LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<>();
        query.eq(TaskPo::getTaskId, taskId);
        TaskPo taskPo = baseMapper.selectOne(query);
        taskPo.setStatus(ToolBoxStatus.DELETE);
        int update = baseMapper.updateById(taskPo);
        return update == 1;
    }

    @Override
    public TaskVo updateTaskById(TaskDto taskDto) {
        TaskVo result = new TaskVo();
        String userId = checkUserIdExist();

        String taskName = taskDto.getTaskName();
        String taskId = taskDto.getTaskId();

        if (StringUtil.isNotBlank(taskId)) {
            LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<TaskPo>();
            query.eq(TaskPo::getTaskId, taskId);
            TaskPo taskPo = baseMapper.selectOne(query);
            if (taskPo == null) {
                // 抛异常
                log.error("task not found");
                return result;
            } else {
                // 由于修改名字是独立逻辑，故先检查是否已存在taskName，如果存在则更新失败
                if (!Objects.equals(taskDto.getTaskName(), taskPo.getTaskName())) {
                    LambdaQueryWrapper<TaskPo> queryByName = new LambdaQueryWrapper<>();
                    queryByName.eq(TaskPo::getTaskName, taskName);
                    // 排除已删除的条目
                    query.notIn(TaskPo::getStatus, ToolBoxStatus.DELETE);
                    TaskPo taskPoByName = baseMapper.selectOne(queryByName);
                    // 重名异常
                    if (taskPoByName != null) {
                        log.error("TaskName already exists.");
                        return result;
                    }
                }
            }
            taskPo = new TaskPo();
            BeanUtils.copyProperties(taskDto, taskPo);

            log.info("[task update] update taskPo:" + taskPo.toString());

            taskPo.setUserId(userId);
            String templateId = taskDto.getTemplateId();

            if (StringUtil.isEmpty(templateId)) {
                taskPo.setStatus(ToolBoxStatus.INIT);
            } else {
                FlowTemplatePo flowTemplatePo = flowTemplateService.queryTemplateById(templateId);
                String data = taskDto.getData();
                if (StringUtil.isEmpty(data)) {
                    taskPo.setStatus(ToolBoxStatus.EDITING);
                    // 未传入data，为第二步选择模板操作，把原有的data字段清空
                    taskPo.setData("");
                } else {
                    taskPo.setStatus(ToolBoxStatus.READY);
                    taskPo.setData(taskDto.getData());
                }
                taskPo.setTaskConfiguration(flowTemplatePo.getTemplateContent());
            }

            taskPo.setUpdateTime(new Date());
            LambdaQueryWrapper<TaskPo> updateQuery = new LambdaQueryWrapper<>();
            updateQuery.eq(TaskPo::getTaskId, taskDto.getTaskId());
            int update = baseMapper.update(taskPo, updateQuery);
            if (update != 1) {
                return result;
            } else {
                BeanUtils.copyProperties(taskPo, result);
                result.setTaskConfiguration(taskDto.getTaskConfiguration());
            }
        }
        return result;
    }

    @Override
    public TaskVo startTask(TaskDto taskDto) {
        TaskVo result = new TaskVo();
        BeanUtils.copyProperties(taskDto, result);
        String userId = checkUserIdExist();

        String taskId = taskDto.getTaskId();

        LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<TaskPo>();
        query.eq(TaskPo::getTaskId, taskId);
        TaskPo taskPo = baseMapper.selectOne(query);
        String data = taskPo.getData();

        if (StringUtil.isBlank(data) || !ToolBoxStatus.READY.equals(taskPo.getStatus())) {
            log.error("task is not ready");
            return result;
        }

        // 严格后置 更新数据库 状态 -》WAITING，真正的执行状态在定时器里面给
        taskPo.setStatus(ToolBoxStatus.WAITING);
        LambdaQueryWrapper<TaskPo> updateQuery = new LambdaQueryWrapper<>();
        updateQuery.eq(TaskPo::getTaskId, taskDto.getTaskId());
        int update = baseMapper.update(taskPo, updateQuery);

        BeanUtils.copyProperties(taskPo, result);
        return result;
    }

    //    todo:
    @Override
    public TaskVo stopTask(int id) {
        return null;
    }

    //    todo:
    @Override
    public TaskVo retryTask(int id) {
        return null;
    }

    @Override
    public List<TaskPo> queryTasksByStatus(ToolBoxStatus toolBoxStatus) {
        if (toolBoxStatus == null) {
//            Log.info("empty toolBoxStatus");
            return new ArrayList<>();
        }

        LambdaQueryWrapper<TaskPo> query = new LambdaQueryWrapper<>();
        query.eq(TaskPo::getStatus, toolBoxStatus);

        return baseMapper.selectList(query);
    }

    private String checkUserIdExist() {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
//            userId = "1589798185264324609";
            throw new BaseException(ResultCode.USER_NOT_EXIST);
        }
        return userId;
    }

    private TaskVo po2vo(TaskPo taskPo) {
        TaskVo taskVo = new TaskVo();
        BeanUtils.copyProperties(taskPo, taskVo);
        return taskVo;
    }

    private Map<String, String> getTaskLog(String taskId, String data) {
        if (StringUtil.isEmpty(data)) {
            log.info("[ToolboxTaskService-updateLog] no log to write, taskId:" + taskId + ", fileId:" + data);
            return new HashMap<>();
        } else {
            String[] dataList = data.split(",");
            Map<String, String> moduleIdLogMap = new HashMap<>();

            for (String fileId : dataList) {
                List<TaskLogPo> taskLogPos = taskLogService.queryTaskLogById(taskId, fileId);
                if (taskLogPos.isEmpty()) {
                    return moduleIdLogMap;
                }
                taskLogPos.sort(Comparator.comparing(TaskLogPo::getUpdateTime));
                Map<String, String> moduleIdFileIdMap = new HashMap<>();
                String logByFileId = "【" + fileId + "】" + "\n";
                for (TaskLogPo taskLogPo : taskLogPos) {
                    String moduleId = taskLogPo.getModuleId();
                    moduleIdFileIdMap.putIfAbsent(moduleId, "");

                    String logContent = moduleIdFileIdMap.get(moduleId);
                    if (StringUtil.isEmpty(logContent)) {
                        logContent += logByFileId;
                    }
                    // 暂时不显示时间戳，已有任务显示时间戳会对不上
                    // logByFileId += taskLogPo.getUpdateTime().toString() + "：";
                    logContent += taskLogPo.getOps() + "，";
                    logContent += TaskLogType.fromCode(Integer.parseInt(taskLogPo.getStatus())) + "，";
                    logContent += "耗时：" + taskLogPo.getRunTime() + "秒" + "\n";

                    moduleIdFileIdMap.put(moduleId, logContent);
                }

                moduleIdFileIdMap.forEach((key, value) -> {
                    String logTotal = moduleIdLogMap.getOrDefault(key, "");
                    moduleIdLogMap.put(key, logTotal + moduleIdFileIdMap.getOrDefault(key, ""));
                });

            }
            return moduleIdLogMap;
        }
    }
}
