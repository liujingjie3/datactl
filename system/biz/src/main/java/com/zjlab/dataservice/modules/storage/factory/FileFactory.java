package com.zjlab.dataservice.modules.storage.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.modules.storage.enums.FileServiceEnum;
import com.zjlab.dataservice.modules.storage.service.FileService;

import java.util.Objects;

public class FileFactory {

    public static FileService getFileService(FileServiceEnum type){

        if (Objects.isNull(type)){
            throw new JeecgBootException("不支持类型");
        }

        FileService fileService = null;
        switch (type){
            case SERVER:
                fileService = SpringUtil.getBean("serverFileService");
                break;
            case HDFS:
                fileService = SpringUtil.getBean("hdfsFileService");
                break;
            default:
                break;
        }

        if (Objects.isNull(fileService)){
            throw new JeecgBootException("不支持类型");
        }
        return fileService;
    }
}
