package com.zjlab.dataservice.config.storage;

import com.zjlab.dataservice.common.constant.CommonConstant;
import com.zjlab.dataservice.common.constant.SymbolConstant;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio文件上传配置文件
 * @author: jeecg-boot
 */
@Slf4j
@Configuration
public class MinioConfig {
    @Value(value = "${jeecg.minio.endpoint}")
    private String endpoint;
    @Value(value = "${jeecg.minio.accessKey}")
    private String accessKey;
    @Value(value = "${jeecg.minio.secretKey}")
    private String secretKey;
    @Value(value = "${jeecg.minio.bucketName}")
    private String bucketName;

    @Bean
    public void initMinio(){
        if(!endpoint.startsWith(CommonConstant.STR_HTTP)){
            endpoint = "http://" + endpoint;
        }
        if(!endpoint.endsWith(SymbolConstant.SINGLE_SLASH)){
            endpoint = endpoint.concat(SymbolConstant.SINGLE_SLASH);
        }
        MinioUtil.setEndpoint(endpoint);
        MinioUtil.setAccessKey(accessKey);
        MinioUtil.setSecretKey(secretKey);
        MinioUtil.setBucketName(bucketName);
        MinioUtil.initMinio(endpoint, accessKey, secretKey);
    }

}
