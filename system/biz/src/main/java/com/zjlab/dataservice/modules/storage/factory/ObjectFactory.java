package com.zjlab.dataservice.modules.storage.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.modules.storage.enums.ObjectServiceEnum;
import com.zjlab.dataservice.modules.storage.service.ObjectService;

import java.util.Objects;

public class ObjectFactory {

    public static ObjectService getObjectService(ObjectServiceEnum type){

        if (Objects.isNull(type)){
            throw new JeecgBootException("不支持类型");
        }

        ObjectService objectService = null;
        switch (type){
            case MINIO:
                objectService = SpringUtil.getBean("minioFileService");
                break;
            default:
                break;
        }

        if (Objects.isNull(objectService)){
            throw new JeecgBootException("不支持类型");
        }
        return objectService;
    }
}
