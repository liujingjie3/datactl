package com.zjlab.dataservice.modules.dataset.model.vo.thematic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Accessors(chain = true)
@Builder
public class ThematicListVo {

    @TableId(type = IdType.AUTO)
    private Integer id;

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
     * 缩略图
     */
    private String thumb;

    /**
     * 区域名称
     */
    private String regionName;

    private String industry;

    private String keywords;

    private String introduction;


    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;

}
