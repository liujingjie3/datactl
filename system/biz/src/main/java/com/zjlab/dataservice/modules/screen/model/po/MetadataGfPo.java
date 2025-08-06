package com.zjlab.dataservice.modules.screen.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName(value = "metadata_gf")
public class MetadataGfPo {

    //字段待补充
    private Integer id;


    private Timestamp receiveTime;

    private Integer status;

    private String fileName;

    private String filePath;

    private String browseFileLocation;

    private String thumbFileLocation;

    private String updateBy;

    private LocalDateTime updateTime;

}
