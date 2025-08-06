//package com.zjlab.dataservice.modules.backup.job;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.zjlab.dataservice.common.util.storage.MinioUtil;
//import com.zjlab.dataservice.modules.backup.model.entity.MinioSource;
//import com.zjlab.dataservice.modules.backup.model.entity.MysqlSource;
//import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
//import com.zjlab.dataservice.modules.backup.service.TaskRecordService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Component
//public class BackupJobBK {
//
//    @Resource
//    private BackupService backupService;
//
//    @Resource
//    private TaskRecordService recordService;
//
//    @Value("${spring.profiles.active}")
//    private String activeProfile;
//
//    //每天0点执行
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void backupMysqlToMinio(){
//        if (Objects.equals(activeProfile, "dev")){
//            //测试环境为windows环境的数据库，切内存不够，暂不操作
//            log.info("environment dev, do nothing and return.");
//            return;
//        }
//        log.info("start backup mysql to S3");
//        //执行备份操作
//        MysqlSource mysqlSource = new MysqlSource();
//        mysqlSource.setHost("10.101.32.14");
//        mysqlSource.setPort(3306);
//        mysqlSource.setUsername("dataservice");
//        mysqlSource.setPassword("Dataservice@ctl123");
//        mysqlSource.setDatabase("dataservice_ctl");
//
//        MinioSource minioSource = new MinioSource();
//        minioSource.setEndpoint("http://10.101.4.101:9000");
//        minioSource.setAccessKey("root");
//        minioSource.setSecretKey("zjlab@7579");
//        minioSource.setBucketName("dspp");
//
//        //生成minio文件路径
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String formattedDate = sdf.format(new Date());
//        String backupFileName = "backup_" + formattedDate + ".sql";
//        String backupPath = "/TJEOS/WORKDIR/BACKUP/MySQL/" + backupFileName;
//        minioSource.setPath(backupPath);
//        boolean result = backupService.MysqlToMinio(mysqlSource, minioSource);
//        TaskRecordPo recordPo = new TaskRecordPo();
//        recordPo.setSourceId(1).setSourceType("mysql")
//                .setDestId(2).setDestType("minio")
//                .setTaskId(1).setCron("0 0 0 * * ?")
//                .setStatus("已结束");
//        recordPo.setSubStatus(result ? "成功" : "失败");
//        JSONObject logInfo = new JSONObject();
//        logInfo.put("path", backupPath);
//        recordPo.setLogInfo(logInfo.toString());
//        recordPo.initBase(true, "admin");
//        recordService.save(recordPo);
//
//        //删除过期文件
//        QueryWrapper<TaskRecordPo> wrapper = new QueryWrapper<>();
//        LocalDateTime localDateTime = LocalDateTime.now().minusDays(15).plusHours(1);
//        wrapper.lambda().le(TaskRecordPo::getUpdateTime, localDateTime);
//        List<TaskRecordPo> list = recordService.list(wrapper);
//        for (TaskRecordPo record : list){
//            JSONObject jsonObject = JSONObject.parseObject(record.getLogInfo());
//            String path = jsonObject.getString("path");
//            try {
//                MinioUtil.removeFile("dspp",path);
//                record.del("admin");
//                recordService.updateById(record);
//                log.info("remove expire file success! path : {}", path);
//            }catch (Exception e){
//                log.error("remove expire file failed! path : {}", path);
//            }
//        }
//
//        log.info("backup and delete expire end.");
//    }
//
//}
