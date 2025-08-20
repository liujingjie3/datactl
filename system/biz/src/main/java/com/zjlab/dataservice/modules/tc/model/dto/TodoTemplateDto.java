package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Data
@ApiModel(value = "TodoTemplateVO对象", description = "实例模板信息表")
public class TodoTemplateDto implements Serializable{
    private static final long serialVersionUID = 3256305580699982020L;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ApiModelProperty(value = "模板主键ID")
    private Long id;

    /**
     * 模板编码
     */
    @ApiModelProperty(value = "模板编码")
    private String code;
    /**
     *模板id
     */
    @ApiModelProperty(value = "模板id")
    private String templateId;

    /**
     * 模板名称
     */
    @NotEmpty
    @ApiModelProperty(value = "模板名称")
    private String templateName;

    /**
     * 模板类型
     */
    @NotEmpty
    @ApiModelProperty(value = "模板类型")
    private String templateType;

    /**
     * 模板属性
     */
    @ApiModelProperty(value = "模板属性")
    @JsonProperty("attrs")
//    private Map<String, Object> attrs;
    private Map<String, Object> attrs;


    /**
     * 模板备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 企业标识
     */
    @ApiModelProperty(value = "企业唯一标识")
    private String corpId;

    /**
     * 微应用标识
     */
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

//    public void setAttrs(String json) {
//        try {
//            this.attrs = MAPPER.readValue(json, Map.class);
//        } catch (Exception e) {
//            this.attrs = Collections.emptyMap();
//        }
//    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }


}
