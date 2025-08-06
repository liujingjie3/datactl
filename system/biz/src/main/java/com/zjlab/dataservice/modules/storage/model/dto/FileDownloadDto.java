package com.zjlab.dataservice.modules.storage.model.dto;

import lombok.Data;

@Data
public class FileDownloadDto {

    //HDFS的相对目录路径，比如：/testDir/a.txt
    private String srcFile;

    //载之后本地文件路径（如果本地文件目录不存在，则会自动创建），比如：D:/test.txt
    private String dstPath;

    private String type;


}
