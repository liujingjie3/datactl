package com.zjlab.dataservice.modules.dataset.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 专题数据表
 *
 * @TableName thematic_data
 */
@TableName(value = "thematic_data")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThematicDataPo extends BasePo implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 产品类别 1-江苏；2-上海；3-浙江
     */
    private Integer province;

    /**
     * 行政区号
     */
    private Integer pac;


    /**
     * 产品类别 1-地物分类；2-变化检测；3-目标识别；4-数据增强；5-大模型应用
     */
    private Integer productType;

    /**
     * 产品形式 1-影像；2-矢量；3-表格
     */
    private Integer format;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 产品性质（带坐标或无坐标）1-带坐标；0-无坐标
     */
    private Integer isCoords;

    /**
     * 数据量大小
     */
    private Long size;

    /**
     * 缩略图
     */
    private String thumb;

    /**
     * 数据ID
     */
    private Integer dataId;

    /**
     * 边界范围
     */
    private String extent;

    /**
     * 是否发布 0-未发布；1-已发布
     */
    private Integer isPublic;

    /**
     * 波段数
     */
    private Integer band;

    /**
     * 行数
     */
    private Integer dataRows;

    /**
     * 列数
     */
    private Integer dataCols;

    /**
     * 任务数量
     */
    private Integer taskCount;

    /**
     * 重要性 0-一般，1-重要
     */
    private Integer important;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 时间范围
     */
    private Date timeRange;

    /**
     * 分辨率
     */
    private Double resolution;

    /**
     * 区域面积
     */
    private Long area;

    /**
     * 最大比例尺
     */
    private Integer zoomMax;

    /**
     * 最小比例尺
     */
    private Integer zoomMin;

    /**
     * 初始比例尺
     */
    private Double zoomInit;

    /**
     * 影像id
     */
    private String rasterId;

    /**
     * 地物分类情况
     */
    private String classification;

    /**
     * 要素数量
     */
    private Integer featureNum;

    /**
     * 目标检测情况,json格式保存目标检测信息
     */
    private String objectDetection;

    /**
     * 查看次数
     */
    private Integer viewCount;

    /**
     * 地图中心
     */
    private String center;

    /**
     * 边界图形,geometry图形要素
     */
    private String boundary;

    /**
     * 样式id
     */
    private String styleId;

    private String industry;

    private String keywords;

    private String introduction;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}