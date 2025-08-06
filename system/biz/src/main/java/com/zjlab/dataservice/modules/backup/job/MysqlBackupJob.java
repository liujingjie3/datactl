package com.zjlab.dataservice.modules.backup.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import com.zjlab.dataservice.common.util.storage.ServerUtil;
import com.zjlab.dataservice.modules.backup.model.entity.JobExecuteResult;
import com.zjlab.dataservice.modules.backup.model.entity.MinioSource;
import com.zjlab.dataservice.modules.backup.model.entity.MysqlSource;
import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
import com.zjlab.dataservice.modules.backup.service.SourceService;
import com.zjlab.dataservice.modules.backup.service.TaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MysqlBackupJob extends BackupJob {

    @Resource
    private TaskRecordService recordService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public MysqlBackupJob(SourceService sourceService) {
        super(sourceService);
    }

    @Override
    public JobExecuteResult backupSourceToDest(){
        JobExecuteResult jobExecuteResult = new JobExecuteResult();
        if (Objects.equals(activeProfile, "dev")){
            //测试环境为windows环境的数据库，切内存不够，暂不操作
            log.info("environment dev, do nothing and return.");
            return jobExecuteResult;
        }
        //执行备份操作
        if (dest instanceof MinioSource){
            log.info("start backup mysql to minio");
            MysqlSource mysqlSource = (MysqlSource) source;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = sdf.format(new Date());
            String backupFileName = "backup_" + formattedDate + ".sql";
            MinioSource minioSource = (MinioSource) dest;
            minioSource.setPath(minioSource.getPath() + "/" + backupFileName);
            try {
                backupToMinio(mysqlSource, minioSource);
                jobExecuteResult.setSuccess(true);
                JSONObject logInfo = new JSONObject();
                logInfo.put("path", minioSource.getPath());
                jobExecuteResult.setLogInfo(logInfo.toString());
            }catch (Exception e){
                jobExecuteResult.setSuccess(false);
                jobExecuteResult.setLogInfo(e.getMessage());
            }
        }
        log.info("backup success!");
        return jobExecuteResult;
    }

    public void backupToMinio(MysqlSource mysqlSource, MinioSource minioSource){
        //生成mysql备份命令
        String backupPath = minioSource.getPath();
        String backupFileName = backupPath.substring(backupPath.lastIndexOf("/")+1);
        String cmd = String.format("/usr/bin/mysqldump -h %s -u %s -p%s %s > %s", mysqlSource.getHost(), mysqlSource.getUsername(), mysqlSource.getPassword(),
                mysqlSource.getDatabase(), backupFileName);

        //执行命令
        String result = ServerUtil.execute(cmd);
        log.info("execute dump cmd. result : {}",result);

        //上传本地文件到minio
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(backupFileName);
            MinioUtil.uploadFile(minioSource.getBucketName(), backupPath, fileInputStream);
            File file = new File(backupFileName);
            file.delete();
            log.info("upload backup file to minio success.");
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public boolean delExpire(String bucketName, List<String> pathList, int expireDays){
        //删除过期文件
        QueryWrapper<TaskRecordPo> wrapper = new QueryWrapper<>();
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(expireDays).plusHours(1);
        wrapper.lambda().le(TaskRecordPo::getUpdateTime, localDateTime);
        List<TaskRecordPo> list = recordService.list(wrapper);
        for (TaskRecordPo record : list){
            JSONObject jsonObject = JSONObject.parseObject(record.getLogInfo());
            String path = jsonObject.getString("path");
            try {
                MinioUtil.removeFile("dspp",path);
                record.del("admin");
                recordService.updateById(record);
                log.info("remove expire file success! path : {}", path);
            }catch (Exception e){
                log.error("remove expire file failed! path : {}", path);
            }
        }
        return true;
    }
}
