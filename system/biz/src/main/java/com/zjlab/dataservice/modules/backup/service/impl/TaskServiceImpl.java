package com.zjlab.dataservice.modules.backup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.backup.enums.SourceTypeEnum;
import com.zjlab.dataservice.modules.backup.enums.TaskStatusEnum;
import com.zjlab.dataservice.modules.backup.enums.TaskSubStatusEnum;
import com.zjlab.dataservice.modules.backup.factory.BackupJobFactory;
import com.zjlab.dataservice.modules.backup.job.BackupJob;
import com.zjlab.dataservice.modules.backup.mapper.TaskMapper;
import com.zjlab.dataservice.modules.backup.model.entity.JobExecuteResult;
import com.zjlab.dataservice.modules.backup.model.po.TaskPo;
import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
import com.zjlab.dataservice.modules.backup.service.SourceService;
import com.zjlab.dataservice.modules.backup.service.TaskRecordService;
import com.zjlab.dataservice.modules.backup.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskPo> implements TaskService {

    @Resource
    private TaskRecordService recordService;

    @Resource
    private SourceService sourceService;

    @Resource
    private BackupJobFactory backupJobFactory;

    @Override
    public void start(Integer id) throws Exception {
        log.info("will start backup task. id = {}", id);
        TaskPo taskPo = baseMapper.selectById(id);
        String sourceType = taskPo.getSourceType();
        String destType = taskPo.getDestType();

        BackupJob backupJob = backupJobFactory.getBackupJob(SourceTypeEnum.getEnumByType(sourceType));
        backupJob.generateSource(taskPo.getSourceId());
        backupJob.generateDest(taskPo.getDestId());
        JobExecuteResult result = backupJob.backupSourceToDest();
        //任务执行记录存表
        TaskRecordPo recordPo = new TaskRecordPo();
        recordPo.setSourceId(taskPo.getSourceId()).setSourceType(taskPo.getSourceType())
                .setDestId(taskPo.getDestId()).setDestType(taskPo.getDestType())
                .setTaskId(taskPo.getId()).setCron(taskPo.getCron())
                .setStatus(TaskStatusEnum.FINISH.getCode());
        recordPo.setSubStatus(result.isSuccess() ? TaskSubStatusEnum.SUCCESS.getCode() : TaskSubStatusEnum.FAIL.getCode());
        recordPo.setLogInfo(result.getLogInfo());
        recordPo.initBase(true, "admin");
        recordService.save(recordPo);
    }

    @Override
    public void delExpire(Integer id) {
        log.info("will expire backup task file. id = {}", id);
    }
}
