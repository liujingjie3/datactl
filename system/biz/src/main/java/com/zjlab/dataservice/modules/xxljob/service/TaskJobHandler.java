package com.zjlab.dataservice.modules.xxljob.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjlab.dataservice.common.util.RestUtil;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManageRecordPo;
import com.zjlab.dataservice.modules.taskplan.model.vo.MetadataVo;
import com.zjlab.dataservice.modules.taskplan.service.MetadataGfService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageRecordService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageService;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import com.zjlab.dataservice.modules.toolbox.po.TaskPo;
import com.zjlab.dataservice.modules.toolbox.service.ToolboxTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TaskJobHandler {

    @Resource
    private ToolboxTaskService toolboxTaskService;

    @XxlJob("startTask")
    public ReturnT<String> startTask() {
        log.info("[XXL JOB]Data process, startTask");
        try {
            List<TaskPo> taskPoList = toolboxTaskService.queryTasksByStatus(ToolBoxStatus.WAITING);
            List<TaskPo> runningTaskPoList = toolboxTaskService.queryTasksByStatus(ToolBoxStatus.RUNNING);

            if (!runningTaskPoList.isEmpty()) {
                log.info("[XXL JOB]start task: running task existed");
                return ReturnT.SUCCESS;
            }

            TaskPo nearestTask = taskPoList.stream()
                    .min(Comparator.comparing(TaskPo::getUpdateTime))
                    .orElse(null);

            if (nearestTask != null) {
                String data = nearestTask.getData();
                String[] dataList = data.split(",");
                String dataName = dataList[0];
                String userId = nearestTask.getUserId();
                String taskId = nearestTask.getTaskId();

                String url = String.format("http://10.107.104.17:8088/dspp/tbx/workflow/gf2flow?file_name=%s&user_id=%s&task_id=%s", dataName, userId, taskId);
                JSONObject postResult = RestUtil.get(url);
                log.info("[XXL JOB]start task: " + postResult);
            }
        } catch (Exception e) {
            return ReturnT.FAIL;
        }

        return ReturnT.SUCCESS;
    }
}