package com.zjlab.dataservice.modules.backup.model.entity;

import lombok.Data;

@Data
public class JobExecuteResult {

    private boolean success = true;

    private String logInfo = "";

}
