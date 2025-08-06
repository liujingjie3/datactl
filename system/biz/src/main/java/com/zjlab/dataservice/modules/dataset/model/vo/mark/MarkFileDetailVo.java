package com.zjlab.dataservice.modules.dataset.model.vo.mark;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MarkFileDetailVo {

    //文件id
    private Integer id;

    //文件名称
    private String name;

    //文件存储路径
    private String fileUrl;

    //文件原始存储路径
    private String oriUrl;

    //缩略图oss地址
    private String thumbUrl;

    //int	用途:1-验证；2-训练；3-测试
    private Integer purpose;

    //文件大小，单位KB
    private Long dataSize;

    //样本尺寸
    private String sampleSize;

    //分辨率
    private String resolution;

    //文件说明
    private String description;

    //分配类别，一个数据字典一个数据集
    private String markType;

    //目标检测标注文件地址
    private String markUrl;

    //标注人userId
    private String marker;

    //标注人姓名
    private String markerName;

    //核查人userId
    private String checker;

    //核查人姓名
    private String checkerName;

    //状态：1-未分配；2-已分配未标注；3-已标注
    private Integer status;

    //审查状态: 0 未审查， 1 审查通过， 2 审查不通过
    private Integer review;

    // 分割图像json地址
    private String segmentUrl;

    //分配时间
    private LocalDateTime createTime;

    //最近更新时间
    private LocalDateTime updateTime;
}
