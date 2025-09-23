package com.zjlab.dataservice.modules.tc.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO 基础类，包含通用的创建及更新字段
 */
@Data
public class TcBaseEntityDto implements Serializable {

    private static final long serialVersionUID = 5014487643533278311L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 删除标识 */
    @TableLogic
    private Boolean delFlag;

    /** 创建人ID */
    private String createBy;
    /** 创建人名称 */
    private String creatorName;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新人ID */
    private String updateBy;
    /** 更新人名称 */
    private String updaterName;
    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 初始化基础信息
     *
     * @param save 是否保存操作
     * @param userId 操作用户
     */
    public void initBase(boolean save, String userId) {
        if (save) {
            this.setDelFlag(Boolean.FALSE);
            this.setCreateBy(userId);
            this.setCreateTime(LocalDateTime.now());
        }
        this.setUpdateBy(userId);
        this.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 标记删除
     *
     * @param userId 操作用户
     */
    public void del(String userId) {
        this.setDelFlag(Boolean.TRUE);
        this.setUpdateBy(userId);
        this.setUpdateTime(LocalDateTime.now());
    }
}
