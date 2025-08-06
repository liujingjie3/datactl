package com.zjlab.dataservice.modules.dataset.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
@TableName(value = "metadata_gf")
public class MetadataGfPo {

    //字段待补充
    private Integer id;

    private Timestamp receiveTime;

    private Integer status;

    private String fileName;

    private String filePath;

    private Float fileSize;

    private String browseFileLocation;

    private String thumbFileLocation;

    private Boolean filterPassed;

    private String marker;

    private String checker;

    private String updateBy;

//    private LocalDateTime updateTime;
    private Date updateTime;


    private Integer review;

    private Float imageGsd;
}
