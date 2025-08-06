package com.zjlab.dataservice.modules.xxljob.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManageRecordPo;
import com.zjlab.dataservice.modules.taskplan.model.vo.MetadataVo;
import com.zjlab.dataservice.modules.taskplan.service.MetadataGfService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageRecordService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TaskPlanJobHandler {

    @Resource
    private TaskManageService taskManageService;

    @Resource
    private MetadataGfService metadataGfService;

    @Resource
    private TaskManageRecordService taskManageRecordService;

    @XxlJob("preplanFileCheck")
    public ReturnT<String> fileCheck() {
        log.info("start check file");
        QueryWrapper<TaskManagePo> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        List<TaskManagePo> list = taskManageService.list(wrapper);
        // 任务状态：1.进行中 2.失败 3.已完成
        for (TaskManagePo record : list) {
            String geo = record.getGeom();
            LocalDateTime createTime = record.getStartTime();
            //如果现在的时间超过创建时间7天，设置这条数据的状态为失败2
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(createTime.plusDays(7))) {
                // 更新 TaskManage 表的 status 为 2（失败）
                record.setStatus(2);
                taskManageService.updateById(record);

                // 创建 TaskManageRecord 对象记录状态更新
                TaskManageRecordPo taskManageRecord = new TaskManageRecordPo();
                taskManageRecord.setTaskId(record.getId());  // 记录任务 ID
                taskManageRecord.setStatus(2);               // 设置修改后的状态为失败
                taskManageRecord.setCreateTime(new Date());  // 设置当前时间为记录创建时间
                taskManageRecordService.save(taskManageRecord); // 插入记录到 TaskManageRecord 表

                log.info("Task ID {} status updated to 2 due to timeout and record inserted into TaskManageRecord", record.getId());
                continue; // 继续处理下一个任务，跳过本次循环的后续逻辑
            }

            MetadataVo metadataVoResult = metadataGfService.queryFile(geo, createTime);
            if (metadataVoResult != null) {
                // 更新 TaskManage 表的 status 为 3
                record.setStatus(3);
                record.setThumbFile(metadataVoResult.getThumbFileLocation());
                taskManageService.updateById(record);
                // 创建 TaskManageRecord 对象
                TaskManageRecordPo taskManageRecord = new TaskManageRecordPo();
                taskManageRecord.setTaskId(record.getId());  // 记录任务 ID
                taskManageRecord.setStatus(3);               // 设置修改后的状态
                taskManageRecord.setCreateTime(new Date());  // 设置当前时间为记录创建时间
                // 插入记录到 TaskManageRecord 表
                taskManageRecordService.save(taskManageRecord);
                log.info("Task ID {} status updated to 3 and record inserted into TaskManageRecord", record.getId());
            }

        }
        return ReturnT.SUCCESS;
    }
}