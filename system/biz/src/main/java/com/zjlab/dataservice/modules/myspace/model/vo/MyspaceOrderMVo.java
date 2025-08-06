package com.zjlab.dataservice.modules.myspace.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据订单表
 *
 * @TableName myspace_order
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyspaceOrderMVo {

    private Integer id;

    private String userName;

    private String talentName;
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

    private List<String> comment;
//    private String imageIds;
    /**
     * 订单状态:1审核中;2已完成
     */
    @ApiModelProperty("订单状态:1审核中;2已完成")
    private Integer orderStatus;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
