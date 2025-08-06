package com.zjlab.dataservice.config.storage;

import com.zjlab.dataservice.common.util.storage.HdfsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HdfsConfig {

//    private String defaultHdfsUrl = "hdfs://10.101.4.100:9870";
    private final String defaultHdfsUrl = "hdfs://node1:9000";

    @Bean
    public void getHbaseService(){
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("fs.defaultFS",defaultHdfsUrl);
        conf.set("dfs.replication", "1");
        HdfsUtil.setConf(conf);
        HdfsUtil.setDefaultHdfsUrl(defaultHdfsUrl);
    }
}
