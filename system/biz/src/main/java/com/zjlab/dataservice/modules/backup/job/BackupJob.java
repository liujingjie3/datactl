package com.zjlab.dataservice.modules.backup.job;

import com.zjlab.dataservice.common.util.encryption.AesEncryptUtil;
import com.zjlab.dataservice.modules.backup.enums.SourceTypeEnum;
import com.zjlab.dataservice.modules.backup.model.entity.JobExecuteResult;
import com.zjlab.dataservice.modules.backup.model.entity.MinioSource;
import com.zjlab.dataservice.modules.backup.model.entity.MysqlSource;
import com.zjlab.dataservice.modules.backup.model.entity.PostgreSqlSource;
import com.zjlab.dataservice.modules.backup.model.po.SourcePo;
import com.zjlab.dataservice.modules.backup.service.SourceService;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class BackupJob {

    //源数据源
    public Object source;
    //目的数据源
    public Object dest;
    private SourceService sourceService;

    public BackupJob(SourceService sourceService){
        this.sourceService = sourceService;
    }

    public JobExecuteResult backupSourceToDest(){
        log.info("will execute backup source to dest");
        return new JobExecuteResult();
    }

    public void generateSource(Integer id) throws Exception {
        log.info("will generate source entity.");
        SourcePo sourcePo = sourceService.getById(id);
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.MYSQL.getType())){
            MysqlSource mysqlSource = new MysqlSource();
            mysqlSource.setHost(sourcePo.getIp())
                    .setHostUsername(sourcePo.getHostUsername())
                    .setHostPwd(AesEncryptUtil.desEncrypt(sourcePo.getHostPwd()))
                    .setPort(3306)
                    .setUsername(sourcePo.getDbUsername())
                    .setPassword(AesEncryptUtil.desEncrypt(sourcePo.getDbPwd()))
                    .setDatabase(sourcePo.getDbName());
            this.source = mysqlSource;
        }
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.POSTGRESQL.getType())){
            PostgreSqlSource postgreSqlSource = new PostgreSqlSource();
            postgreSqlSource.setHost(sourcePo.getIp())
                    .setHostUsername(sourcePo.getHostUsername())
                    .setHostPwd(AesEncryptUtil.desEncrypt(sourcePo.getHostPwd()))
                    .setPort(3306)
                    .setUsername(sourcePo.getDbUsername())
                    .setPassword(AesEncryptUtil.desEncrypt(sourcePo.getDbPwd()))
                    .setDatabase(sourcePo.getDbName());
            this.source = postgreSqlSource;
        }
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.MINIO.getType())){
            MinioSource minioSource = new MinioSource();
            minioSource.setEndpoint(sourcePo.getEndpoint())
                    .setSecretKey(AesEncryptUtil.desEncrypt(sourcePo.getSecretKey()))
                    .setAccessKey(sourcePo.getAccessKey())
                    .setBucketName(sourcePo.getBucketName())
                    .setPath(sourcePo.getObjectPath());
            this.source = minioSource;
        }
        log.info("generate source success! source={}", source);
    }

    public void generateDest(Integer id) throws Exception {
        log.info("will generate dest entity.");
        SourcePo sourcePo = sourceService.getById(id);
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.MYSQL.getType())){
            MysqlSource mysqlSource = new MysqlSource();
            mysqlSource.setHost(sourcePo.getIp())
                    .setHostUsername(sourcePo.getHostUsername())
                    .setHostPwd(AesEncryptUtil.desEncrypt(sourcePo.getHostPwd()))
                    .setPort(3306)
                    .setUsername(sourcePo.getDbUsername())
                    .setPassword(AesEncryptUtil.desEncrypt(sourcePo.getDbPwd()))
                    .setDatabase(sourcePo.getDbName());
            this.dest = mysqlSource;
        }
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.POSTGRESQL.getType())){
            PostgreSqlSource postgreSqlSource = new PostgreSqlSource();
            postgreSqlSource.setHost(sourcePo.getIp())
                    .setHostUsername(sourcePo.getHostUsername())
                    .setHostPwd(AesEncryptUtil.desEncrypt(sourcePo.getHostPwd()))
                    .setPort(3306)
                    .setUsername(sourcePo.getDbUsername())
                    .setPassword(AesEncryptUtil.desEncrypt(sourcePo.getDbPwd()))
                    .setDatabase(sourcePo.getDbName());
            this.dest = postgreSqlSource;
        }
        if (Objects.equals(sourcePo.getType(), SourceTypeEnum.MINIO.getType())){
            MinioSource minioSource = new MinioSource();
            minioSource.setEndpoint(sourcePo.getEndpoint())
                    .setSecretKey(AesEncryptUtil.desEncrypt(sourcePo.getSecretKey()))
                    .setAccessKey(sourcePo.getAccessKey())
                    .setBucketName(sourcePo.getBucketName())
                    .setPath(sourcePo.getObjectPath());
            this.dest = minioSource;
        }
        log.info("generate dest success! dest={}", dest);
    }

}
