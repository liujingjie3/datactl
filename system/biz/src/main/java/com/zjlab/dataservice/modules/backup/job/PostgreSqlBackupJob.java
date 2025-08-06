package com.zjlab.dataservice.modules.backup.job;

import com.zjlab.dataservice.modules.backup.service.SourceService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSqlBackupJob extends BackupJob{

    public PostgreSqlBackupJob(SourceService sourceService) {
        super(sourceService);
    }
}
