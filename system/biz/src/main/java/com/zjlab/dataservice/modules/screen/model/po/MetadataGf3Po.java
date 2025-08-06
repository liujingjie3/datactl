package com.zjlab.dataservice.modules.screen.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName(value = "metadata_gf3")
public class MetadataGf3Po {

    //字段待补充
    private Integer id;

    private Timestamp receiveTime;

    private Integer status;

    private String fileName;

    private String filePath;

    private String browseFileLocation;

    private String thumbFileLocation;

    private Boolean filterPassed;

    private String updateBy;

    private LocalDateTime updateTime;

    private Integer review;
}
