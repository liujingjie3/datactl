package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tc_todo_instance")
@ApiModel(value = "TodoInstance对象", description = "任务流程实例信息表")
public class TodoInstance extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实例编码
     */
    @ApiModelProperty(value = "实例编码")
    private String code;

    @ApiModelProperty(value = "实例ID")
    private String instanceId;

    /**
     *模板id
     */
    @ApiModelProperty(value = "模板id")
    private String templateId;
    /**
     * 实例标题
     */
    @ApiModelProperty(value = "实例标题")
    private String title;
    /**
     * 实例名称
     */
    @ApiModelProperty(value = "实例名称")
    private String instanceName;
    /**
     * 实例类型
     */
    @ApiModelProperty(value = "实例类型")
    private String instanceType;
    /**
     * 发起人id
     */
    @ApiModelProperty(value = "发起人id")
    private String fromUserId;

    /**
     * 流程发起时间
     */
    @ApiModelProperty(value = "流程发起时间")
    private Long createOn;
    /**
     * 流程模板编码
     */
    @ApiModelProperty(value = "流程模板编码")
    private String templateCode;
    /**
     * 实例属性json存储
     */
    @ApiModelProperty(value = "实例属性json存储")
    private String instanceAttr;
    /**
     * 标识， -1取消，0运行中，1完成
     */
    @ApiModelProperty(value = "标识")
    private Integer flag;
    /**
     * 结果标识， -1拒绝，0运行，1同意
     */
    @ApiModelProperty(value = "结果标识")
    private Integer result;
    /**
     * pc跳转url
     */
    @ApiModelProperty(value = "pc跳转url")
    private String pcUrl;
    /**
     * h5跳转url
     */
    @ApiModelProperty(value = "h5跳转url")
    private String h5Url;

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
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
