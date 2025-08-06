package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjlab.dataservice.common.api.page.PageRequest;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(value = "TodoInstanceVO对象", description = "任务流程实例信息表")
public class TodoInstanceDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1553193915043860107L;

    @ApiModelProperty(value = "实例标题")
    private String title;

    @ApiModelProperty(value = "实例编码")
    private String code;

    @ApiModelProperty(value = "实例ID--不用")
    private String instanceId;

    /**
     *模板id
     */
    @ApiModelProperty(value = "模板id")
    private String templateId;

    @ApiModelProperty(value = "实例类型")
    private String instanceType;

    @ApiModelProperty(value = "实例名称")
    private String instanceName;

    @ApiModelProperty(value = "发往人ID")
    private String fromUserId;

    /**
     * 流程发起时间
     */
    @ApiModelProperty(value = "流程发起时间戳")
    private Long createOn;

    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    @ApiModelProperty(value = "实例属性")
    private Map<String, Object> attrs;

    private String instanceAttr;

    private String ext;

    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;

    @ApiModelProperty(value = "h5跳转url")
    private String h5Url;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    private Integer flag;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1取消")
    private Integer result;


    @ApiModelProperty(value = "客户端标识")
    private String corpId;

    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    @ApiModelProperty(value = "备注")
    private String remark;

}
