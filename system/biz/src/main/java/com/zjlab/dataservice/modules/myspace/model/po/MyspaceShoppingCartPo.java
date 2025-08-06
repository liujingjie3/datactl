package com.zjlab.dataservice.modules.myspace.model.po;

import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BaseIntPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
* 数据申请列表
* @TableName myspace_shopping_cart
*/
@Data
@TableName("myspace_shopping_cart")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyspaceShoppingCartPo extends BaseIntPo {


    /**
    * 用户id
    */

    private String userId;
    /**
    * 对应影像元数据表的file_id
    */
    private Integer imageId;

    private String thumbFileLocation;
    /**
    * 文件名
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("文件名")
    @Length(max= 100,message="编码长度不能超过100")
    private String fileName;
    /**
    * 卫星
    */
    @Size(max= 10,message="编码长度不能超过10")
    @ApiModelProperty("卫星")
    @Length(max= 10,message="编码长度不能超过10")
    private String satellite;
    /**
    * 传感器
    */
    @Size(max= 10,message="编码长度不能超过10")
    @ApiModelProperty("传感器")
    @Length(max= 10,message="编码长度不能超过10")
    private String senser;

    /**
    * 分辨率
    */
    @Size(max= 10,message="编码长度不能超过10")
    @ApiModelProperty("分辨率")
    @Length(max= 10,message="编码长度不能超过10")
    private String resolution;
    /**
    * 分辨率
    */
    @Size(max= 10,message="编码长度不能超过10")
    @ApiModelProperty("分辨率")
    @Length(max= 10,message="编码长度不能超过10")
    private String level;

}
