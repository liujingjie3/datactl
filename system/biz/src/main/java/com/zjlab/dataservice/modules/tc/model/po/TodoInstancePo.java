package com.zjlab.dataservice.modules.tc.model.po;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TodoInstancePo implements Serializable {

    private static final long serialVersionUID = 1553193915043860107L;

    private Long id;

    @ApiModelProperty(value = "实例标题")
    @NotBlank(message = "title不能为空")
    private String title;

    @JsonProperty("instance_code")
    @NotBlank(message = "instance_code不能为空")
    @ApiModelProperty(value = "实例编码")
    private String code;

    @ApiModelProperty(value = "实例ID")
    private String instanceId;

    /**
     *模板id
     */
    @ApiModelProperty(value = "模板id")
    @JsonProperty("template_id")
    private String templateId;

    @NotBlank(message = "instance_type不能为空")
    @JsonProperty("instance_type")
    @ApiModelProperty(value = "实例类型")
    private String instanceType;

    @NotEmpty
    @JsonProperty("instance_name")
    @ApiModelProperty(value = "实例名称")
    private String instanceName;

    @NotBlank(message = "from_user_id不能为空")
    @JsonProperty("from_user_id")
    @ApiModelProperty(value = "发往人ID")
    private String fromUserId;

    /**
     * 流程发起时间
     */
    @ApiModelProperty(value = "流程发起时间戳")
    @JsonProperty("create_on")
    @NotNull(message = "create_on不能为空")
    private Long createOn;

    @JsonProperty("template_code")
    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    @ApiModelProperty(value = "实例属性")
    private Map<String, Object> attrs;

    private String instanceAttr;

    private String ext;

    @JsonProperty("pc_url")
    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;

    @JsonProperty("h5_url")
    @ApiModelProperty(value = "h5跳转url")
    private String h5Url;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1拒绝")
    private Integer flag;

    @ApiModelProperty(value = "状态标识，0进行中，1完成，-1取消")
    private Integer result;


    @JsonProperty("corp_id")
    @ApiModelProperty(value = "客户端标识")
    private String corpId;

    @JsonProperty("agent_id")
    @ApiModelProperty(value = "客户端应用标识")
    private String agentId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
