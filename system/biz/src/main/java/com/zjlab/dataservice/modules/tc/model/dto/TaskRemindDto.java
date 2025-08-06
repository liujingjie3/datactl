package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRemindDto extends PageRequest implements Serializable {

    private String userId;
    private Long count;

}
