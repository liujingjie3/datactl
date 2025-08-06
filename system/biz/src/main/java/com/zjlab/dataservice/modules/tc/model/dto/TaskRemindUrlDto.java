package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class TaskRemindUrlDto implements Serializable {

    private static final long serialVersionUID = 4360118954402900207L;
    private String url;

}
