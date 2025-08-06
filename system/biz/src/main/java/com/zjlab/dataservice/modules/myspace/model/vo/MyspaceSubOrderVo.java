package com.zjlab.dataservice.modules.myspace.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
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
public class MyspaceSubOrderVo {


    private  String remark ;
    private Integer orderStatus ;
    private LocalDateTime orderTime;
    private List<MyspaceSubOrderPo> list;


}
