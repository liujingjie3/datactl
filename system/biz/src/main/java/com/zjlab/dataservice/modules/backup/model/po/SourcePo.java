package com.zjlab.dataservice.modules.backup.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("backup_source")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SourcePo extends BasePo {

    //数据源名称
    private String name;
    //数据源类型
    private String type;
    //服务器/数据库ip
    private String ip;
    //服务器用户名
    private String hostUsername;
    //服务器密码
    private String hostPwd;
    //数据库用户名
    private String dbUsername;
    //数据库密码
    private String dbPwd;
    //指定数据库
    private String dbName;
    //minio-endpoint
    private String endpoint;
    //minio-ak
    private String accessKey;
    //minio-sk
    private String secretKey;
    //minio
    private String bucketName;
    //minio保存路径
    private String objectPath;
}
