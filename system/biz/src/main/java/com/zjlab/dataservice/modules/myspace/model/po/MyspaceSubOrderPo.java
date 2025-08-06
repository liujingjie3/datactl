package com.zjlab.dataservice.modules.myspace.model.po;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BaseIntPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
* 子订单表
* @TableName myspace_sub_order
*/

@Data
@TableName("myspace_sub_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyspaceSubOrderPo extends BaseIntPo {

    /**
    * 订单编号
    */
    private String orderId;


    private Integer imageId;
    /**
    * 用户id,对应用户信息表的user_id
    */

    private String userId;
    /**
    * 订单状态:1审核中;2审核通过;3审核不通过
    */
    @ApiModelProperty("订单状态:1审核中;2审核通过;3审核不通过;4已失效")
    private Integer subOrderStatus;
    /**
    * 缩略图文件位置
    */

    private String thumbFileLocation;
    /**
    * 文件名
    */

    private String fileName;


    /**
    * 卫星名称
    */

    private String satellite;
    /**
    * 传感器类型
    */

    private String senser;

    private String resolution;    /**
    * 云量百分比
    */
    @ApiModelProperty("云量百分比")
    private Float cloudPercent;
    /**
    * 接收日期
    */
    @ApiModelProperty("接收日期")
    private LocalDateTime receiveDate;
    /**
    * 处理等级
    */

    private String level;
    /**
    * 审批人
    */

    private String approver;
    /**
    * 审批意见
    */
    private String approvalOpinion;

    private String fileUrl;

    /**
    * 审批时间
    */
    @ApiModelProperty("审批时间")
    private LocalDateTime approvalTime;

}
