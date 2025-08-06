package com.zjlab.dataservice.modules.myspace.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjlab.dataservice.common.system.base.entity.BaseIntPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.PostLoad;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据订单表
 *
 * @TableName myspace_order
 */
@Data
@TableName("myspace_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyspaceOrderPo extends BaseIntPo {


    /**
     * 订单编号
     */
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("订单编号")
    @Length(max = 255, message = "编码长度不能超过255")
    private String orderId;
    /**
     * 子订单数
     */
    @ApiModelProperty("子订单数")
    private Integer orderNum;
    /**
     * 用户id
     */
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("用户id")
    @Length(max = 50, message = "编码长度不能超过50")
    private String userId;
    /**
     * 备注，具体描述数据用途
     */
    @Size(max = 1000, message = "编码长度不能超过1000")
    @ApiModelProperty("备注，具体描述数据用途")
    @Length(max = 1000, message = "编码长度不能超过1,000")
    private String remark;

    private String comment;
    private String imageIds;
    /**
     * 订单状态:1审核中;2已完成
     */
    @ApiModelProperty("订单状态:1审核中;2已完成")
    private Integer orderStatus;
    /**
     * 订单生成时间
     */
    @JsonIgnore
    @ApiModelProperty("订单生成时间")
    private LocalDateTime orderTime;
    /**
     * 审批人
     */
    @JsonIgnore
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("审批人")
    @Length(max = 50, message = "编码长度不能超过50")
    private String approver;
    /**
     * 审批意见
     */
    @JsonIgnore
    @Size(max = 255, message = "编码长度不能超过255")
    @ApiModelProperty("审批意见")
    @Length(max = 255, message = "编码长度不能超过255")
    private String approvalOpinion;
    /**
     * 审批时间
     */
    @JsonIgnore
    @ApiModelProperty("审批时间")
    private LocalDateTime approvalTime;
    /**
     * 审批结果:1通过;2不通过
     */
    @JsonIgnore
    @ApiModelProperty("审批结果:1通过;2不通过")
    private Integer approvalResult;


}
