package com.zjlab.dataservice.modules.dataset.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName("dataset_mark_file")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DatasetMarkFilePo extends BasePo {

    //文件名称
    private String name;

    //文件存储路径
    private String fileUrl;

    //文件原始存储路径
    private String oriUrl;

    //缩略图oss地址
    private String thumbUrl;

    //图文任务用途：1-验证; 2-训练; 3-测试; 4-其他
    private Integer purpose;

    //分类任务用途：1-验证; 2-训练; 3-测试; 4-其他
    private Integer classifyPurpose;

    //分割任务用途：1-验证; 2-训练; 3-测试; 4-其他
    private Integer segmentPurpose;

    //数据集大小，单位KB
    private Long dataSize;

    //样本尺寸
    private String sampleSize;

    //分辨率
    private String resolution;

    //文件说明
    private String description;

    //二选一；分类类别，对应数据字典，不用数据集不一样
    private String markType;

    //目标检测标注文件地址url
    private String markUrl;

    //标注人userId
    private String marker;

    //核查人userId
    private String checker;

    //状态：0-未分配，1-已分配，未标注，2-已标注，3-已检查（预留），4-已完成
    private Integer status;

    //审查状态: 0 未审查， 1 审查通过， 2 审查不通过
    private Integer review;

    // 目标分割文件地址
    private String segmentUrl;

    // 分类任务状态
    private Integer classifyStatus;

    // 分类任务审查状态: 0 未审查， 1 审查通过， 2 审查不通过
    private Integer classifyReview;

    // 分类任务小组长
    private String classifyChecker;

    // 分类任务组员
    private String classifyMarker;

    // 分类任务更新人
    private String classifyUpdateBy;

    // 分类任务更新时间
    private LocalDateTime classifyUpdateTime;

    // 分割任务状态
    private Integer segmentStatus;

    // 分割任务审查状态: 0 未审查， 1 审查通过， 2 审查不通过
    private Integer segmentReview;

    // 分割任务小组长
    private String segmentChecker;

    // 分割任务组员
    private String segmentMarker;

    // 分割任务更新人
    private String segmentUpdateBy;

    // 分割任务更新时间
    private LocalDateTime segmentUpdateTime;
}
