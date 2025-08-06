package com.zjlab.dataservice.modules.backup.factory;

import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.modules.backup.enums.SourceTypeEnum;
import com.zjlab.dataservice.modules.backup.job.BackupJob;
import com.zjlab.dataservice.modules.backup.job.MysqlBackupJob;
import com.zjlab.dataservice.modules.backup.job.PostgreSqlBackupJob;
import com.zjlab.dataservice.modules.backup.service.SourceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class BackupJobFactory {


    @Resource
    private SourceService sourceService;

    public BackupJob getBackupJob(SourceTypeEnum type){
        if (Objects.isNull(type)){
            throw new JeecgBootException("不支持类型");
        }

        BackupJob backupJob = null;
        switch (type){
            case MYSQL:
                backupJob = new MysqlBackupJob(sourceService);
                break;
            case POSTGRESQL:
                backupJob = new PostgreSqlBackupJob(sourceService);
                break;
            default:
                break;
        }
        if (Objects.isNull(backupJob)){
            throw new JeecgBootException("不支持类型");
        }
        return backupJob;
    }
}
