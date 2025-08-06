package com.zjlab.dataservice.modules.storage.model.dto;

import lombok.Data;

@Data
public class FileUploadDto {
    //本地文件路径，比如：D:/test.txt
    private String srcFile;

    //HDFS的相对目录路径，比如：/testDir
    //minio上的文件夹目录
    private String dstPath;

    private String bucketName;

    //是否删除源文件
    private boolean delSrc;

    //是否覆盖hdfs上的文件
    private boolean overWrite;

    //上传类型
    private String type;

    //偏移量
    private Long offset;
}
