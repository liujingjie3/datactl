package com.zjlab.dataservice.modules.tc.model.vo;

import com.zjlab.dataservice.common.api.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "TodoTemplateVO对象", description = "实例模板信息表")
@Builder
public class TemplateQueryListVo extends PageRequest implements Serializable{

    private static final long serialVersionUID = -5228671930306828376L;
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "状态，0：未发布，1：已发布")
    private Integer flag;

    @ApiModelProperty(value = "模板类型")
    private Integer nodeCount;

    @ApiModelProperty(value = "用户ID")
    private String userName;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
