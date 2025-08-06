package com.zjlab.dataservice.modules.tc.model.entity;

import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(value = "MsgInstanceVO对象", description = "消息实例记录表")
public class MsgInstance extends BasePo implements Serializable {
    private static final long serialVersionUID = 7775132699321438122L;
    /**
     * 消息编码
     */
    @ApiModelProperty(value = "消息编码")
    private String msgCode;
    /**
     * 消息标题
     */
    @ApiModelProperty(value = "消息标题")
    @NotBlank(message = "title不能为空")
    private String msgTitle;
    /**
     * 消息模板id
     */
    @ApiModelProperty(value = "消息模板id")
    @NotBlank(message = "template_id不能为空")
    private String templateId;
    /**
     * 消息模板赋值对象json
     */
    @ApiModelProperty(value = "消息模板赋值对象json")
    private Map<String,String> templateParam;

    /**
     * 消息发送者编码
     */
    @ApiModelProperty(value = "消息发送者编码")
    private String productCode;
    /**
     * 消息发送方名称
     */
    @ApiModelProperty(value = "消息发送方名称")
    private String productName;
    /**
     * 接受者用户id集合，英文逗号分隔
     */
    @ApiModelProperty(value = "接受者用户id集合，英文逗号分隔")
    private String[] receiveUserIdList;
    /**
     * 接受者部门id集合,英文逗号分隔
     */
    @ApiModelProperty(value = "接受者部门id集合,英文逗号分隔")
    private String[] receiveDeptIdList;
    /**
     * 接受者手机号集合，英文逗号分隔
     */
    @ApiModelProperty(value = "接受者手机号集合，英文逗号分隔")
    private String[] receivePhoneList;
    /**
     * 接受者邮箱集合，英文逗号分隔
     */
    @ApiModelProperty(value = "接受者邮箱集合，英文逗号分隔")
    private String[] receiveEmailList;
    /**
     * 接受者抄送邮箱集合，英文逗号分隔
     */
    @ApiModelProperty(value = "接受者抄送邮箱集合，英文逗号分隔")
    private String[] receiveCopyEmailList;
    /**
     * pc端跳转地址
     */
    @ApiModelProperty(value = "pc端跳转地址")
    private String pcUrl;
    /**
     * 手机端调转地址
     */
    @ApiModelProperty(value = "手机端调转地址")
    private String phoneUrl;

    /**
     * 是否发送给企业所有全部用户，默认为false
     */
    @ApiModelProperty(value = "是否发送给企业所有全部用户，默认为false")
    private Boolean toAllUser;

    @ApiModelProperty(value = "应用ID")
    private String agentId;

    @ApiModelProperty(value = "公司ID")
    private String corpId;
}
