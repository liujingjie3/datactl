package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjlab.dataservice.common.api.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "TodoTemplateVO对象", description = "实例模板信息表")
public class TodoTemplateQueryDto extends PageRequest implements Serializable{

    private static final long serialVersionUID = -5228671930306828376L;
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    private String templateId;

    private String templateCode;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板类型")
    private String templateType;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "模板编码集合List")
    private List<String> codes;

}
