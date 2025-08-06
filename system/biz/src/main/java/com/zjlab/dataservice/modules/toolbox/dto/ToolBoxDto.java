package com.zjlab.dataservice.modules.toolbox.dto;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.modules.toolbox.enumerate.TemplateType;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ToolBoxDto extends PageRequest {

    private String status;

    private String taskName;

    private String templateName;

    private TemplateType templateType;

}
