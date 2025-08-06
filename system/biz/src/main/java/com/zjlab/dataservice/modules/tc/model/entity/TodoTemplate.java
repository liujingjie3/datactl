package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import java.util.Map;

@Data
@TableName("tc_todo_template")
@ApiModel(value = "TodoTemplate对象", description = "实例模板信息表")
public class TodoTemplate extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    private String templateType;
    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String templateName;
    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private Integer flag;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 模板属性
     */
    @ApiModelProperty(value = "模板属性")
    private Map<String, Object> templateAttr;
    /**
     * 客户端标识
     */
    @ApiModelProperty(value = "客户端标识")
    private String corpId;
    /**
     * 客户端应用标识
     */
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;
    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段")
    private String ext;

}
