package com.zjlab.dataservice.common.system.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePo {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "租户id")
    private Integer tenantId;

    @ApiModelProperty(value = "是否删除")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 初始化基础信息
     * @param save 是否是保存的场景
     */
    public void initBase(boolean save,String userId) {
        if (save) {
            this.setDelFlag(false);
            this.setCreateBy(userId);
            this.setCreateTime(LocalDateTime.now());
        }
        this.setUpdateBy(userId);
        this.setUpdateTime(LocalDateTime.now());
    }

    public void del(String userId){
        this.setDelFlag(true);
        this.setUpdateBy(userId);
        this.setUpdateTime(LocalDateTime.now());
    }

}

