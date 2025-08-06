package com.zjlab.dataservice.modules.backup.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MysqlSource {

    //服务器/数据库ip
    private String host;
    //服务器用户名
    private String hostUsername;
    //服务器密码
    private String hostPwd;
    //数据库端口：3306
    private Integer port = 3306;
    //数据库用户名
    private String username;
    //数据库密码
    private String password;
    //指定数据库
    private String database;

}
