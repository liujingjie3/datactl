package com.zjlab.dataservice.modules.dataset.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 类别表
 *
 * @TableName thematic_types
 */
@TableName(value = "thematic_types")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThematicTypesPo implements Serializable {
    /**
     * id，自增
     */
    @TableId
    private String id;
    /**
     * 中文名称
     */
    private String nameCh;
    /**
     * 英文名称
     */
    private String nameEn;
    /**
     * 类别
     */
    private String type;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 父编码
     */
    private String parent;
    /**
     * 描述
     */
    private String description;
    /**
     * 图标
     */
    private String icon;
    /**
     * 背景图
     */
    private String background;
    /**
     * 最小分辨率
     */
    private Double minRes;
    /**
     * 最大分辨率
     */
    private Double maxRes;
    /**
     * 最佳分辨率
     */
    private Double bestRes;
    /**
     * 最小图片尺寸
     */
    private Integer minImgSize;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 区域
     */
    private String region;
    /**
     * 季节
     */
    private String season;
    /**
     * 波段数
     */
    private Integer bandCount;
    /**
     * 输入数据数量
     */
    private Integer inputCount;
    /**
     * 是否大图
     */
    private Integer largeImage;
    /**
     * 填充色
     */
    private String fillColor;
    /**
     * 边框颜色
     */
    private String borderColor;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}