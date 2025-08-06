package com.zjlab.dataservice.common.es;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * es 工具类
 * 01-索引管理 ：创建-删除-别名-打开-关闭-修改-查看
 * 02-文档管理 ：创建-查看-修改-删除-批量操作
 * 03-映射管理 ：创建/修改
 * 04-索引模板 ：
 * 05-集群管理 ：
 */
@Data
@Slf4j
public class EsUtils {

    private static String ip;
    private static String port;
    private static String username;
    private static String password;

    /*************************************  Bucket Operation  *************************************/
}
