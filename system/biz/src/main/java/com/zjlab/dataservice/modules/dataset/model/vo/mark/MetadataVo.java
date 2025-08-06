package com.zjlab.dataservice.modules.dataset.model.vo.mark;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MetadataVo {

    // 文件id
    private Integer id;

    // 文件名称
    private String name;

    // 状态 1-未分配；2-已分配未标注；3-已标注
    private Integer status;

    // 文件存储路径(放大大图）
    private String browseFileUrl;

    // 文件存储路径(缩略图)
    private String thumbFileUrl;

    // 是否通过筛选 0未通过 1通过 -1未筛选
    private Boolean filterPassed;

    // 标注人userId
    private String marker;

    // 标注人姓名（查不到名字返回null）
    private String markerName;

    // 核查人userId
    private String checker;

    // 核查人姓名（查不到名字返回null）
    private String checkerName;

    //审查状态: 0 未审查， 1 审查通过， 2 审查不通过
    private Integer review;

    //分配时间
    private LocalDateTime createTime;

    //最近更新时间
    private LocalDateTime updateTime;
}
