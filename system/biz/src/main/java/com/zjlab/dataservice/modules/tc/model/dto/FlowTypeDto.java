package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlowTypeDto implements Serializable {

    private Long id;

    private String typeCode;

    private String typeName;

}
