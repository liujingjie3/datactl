package com.zjlab.dataservice.modules.taskplan.model.vo;

import lombok.Data;

import java.util.Date;
@Data
public class MetadataVo  {

    private Integer id;

    private Date receiveTime;

    private String fileName;

    private String thumbFileLocation;

    private String browseFileLocation;

    private String dataArchiveFile;

    private boolean isCollected;

}
