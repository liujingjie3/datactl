package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.taskplan.mapper.TaskManageMapper;
import com.zjlab.dataservice.modules.taskplan.model.dto.TaskAddDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.TaskListDto;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.vo.*;
import com.zjlab.dataservice.modules.taskplan.service.OrbitPlanService;
import com.zjlab.dataservice.modules.taskplan.service.RemoteCommandService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【task_manage(预规划数据管理表)】的数据库操作Service实现
 * @createDate 2024-08-07 16:05:24
 */
@Service
public class TaskManageServiceImpl extends ServiceImpl<TaskManageMapper, TaskManagePo>
        implements TaskManageService {

    private static final Integer ongoing = 1;    //进行中
    private static final Integer failure = 3;    //失败
    private static final Integer success = 2;    //成功

    @Autowired
    private RemoteCommandService remoteCommandService;

    @Autowired
    private OrbitPlanService orbitPlanService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTask(TaskAddDto taskAddDto) {
        TaskManagePo taskManagePo = new TaskManagePo();
        BeanUtils.copyProperties(taskAddDto, taskManagePo);
//        taskManagePo.setPreplanGeom(taskAddDto.getPreplanGeom().toString());
        taskManagePo.setSatelliteInfo(taskAddDto.getSatelliteInfo().toString());
        String userId = UserThreadLocal.getUserId();
        taskManagePo.setUserId(userId);
        taskManagePo.initBase(true, userId);
        taskManagePo.setStatus(ongoing);
        int num = baseMapper.insert(taskManagePo);
        if (num <= 0) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }

        // 自动匹配遥控指令并生成轨道计划
        List<String> commands = remoteCommandService.matchCommands(taskManagePo);
        orbitPlanService.generateOrbitPlan(taskManagePo, commands);

        return taskManagePo.getId();
    }

    @Override
    public PageResult<TaskManageVo> qryTaskList(TaskListDto taskListDto) {
        taskListDto.setUserId(UserThreadLocal.getUserId());
        PageResult<TaskManageVo> result = new PageResult<>();
        checkParam(taskListDto);
        IPage<TaskManagePo> page = new Page<>(taskListDto.getPageNo(), taskListDto.getPageSize());
        IPage<TaskManagePo> taskList = baseMapper.qryTaskList(page, taskListDto);

        List<TaskManagePo> listRecords = taskList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        List<TaskManageVo> collect = listRecords.stream().map(TaskManagePo -> {
            TaskManageVo taskManageVo = TaskManageVo.builder()
                    .id(TaskManagePo.getId())
                    .thumbFile(TaskManagePo.getThumbFile())
                    .taskName(TaskManagePo.getTaskName())
                    .taskInfo(TaskManagePo.getTaskInfo())
                    .startTime(TaskManagePo.getStartTime())
                    .endTime(TaskManagePo.getEndTime())
                    .status(TaskManagePo.getStatus())
                    .imageArea(TaskManagePo.getImageArea())
                    .geom(TaskManagePo.getGeom())
                    .preplanGeom(TaskManagePo.getPreplanGeom())
                    .taskType(TaskManagePo.getTaskType())
                    .needPhoto(TaskManagePo.getNeedPhoto())
                    .level(TaskManagePo.getLevel())
                    .satelliteInfo(TaskManagePo.getSatelliteInfo())
                    .build();
            return taskManageVo;
        }).collect(Collectors.toList());

        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    @Override
    public TaskStaticsVo statistics() {
        String userId = UserThreadLocal.getUserId();
        return baseMapper.statistics(userId);
    }

    @Override
    public List<taskVo> listByStatus(int i) {
        List<TaskManagePo> taskManagePos = baseMapper.selectByStatus(i);
        List<taskVo> taskVoList = taskManagePos.stream()
                .map(taskManagePo -> taskVo.builder()
                        .messageType("")    // 消息类型
                        .originator("")      // 来源
                        .recipient("")        // 目的地
                        .creationTime(String.valueOf(taskManagePo.getCreateTime()))  // 任务创建时间
                        .planType("COMMON")          // 任务类型
                        .targetInfoList(taskManagePo.getPreplanGeom())// 成像信息列表
                        .satelliteId(taskManagePo.getSatelliteInfo())    // 卫星ID
                        .planId(String.valueOf(taskManagePo.getId()))              // 成像订单编号
                        .build())
                .collect(Collectors.toList());

        return taskVoList;
    }

    private void checkParam(TaskListDto taskListDto) {
        taskListDto.setPageNo(Optional.ofNullable(taskListDto.getPageNo()).orElse(1));
        taskListDto.setPageSize(Optional.ofNullable(taskListDto.getPageSize()).orElse(10));
        taskListDto.setOrderByType(Optional.ofNullable(taskListDto.getOrderByType()).orElse("asc"));
        taskListDto.setOrderByField(Optional.ofNullable(taskListDto.getOrderByField()).orElse("create_time"));
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }
}




