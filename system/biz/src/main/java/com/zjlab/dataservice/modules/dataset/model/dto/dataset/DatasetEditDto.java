package com.zjlab.dataservice.modules.dataset.model.dto.dataset;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DatasetEditDto {

    @NotNull
    private Integer id;

    //数据集名称
    private String name;

    //英文标题，查询url用
    private String title;

    //简介
    private String  introduction;

    //任务类型，1-目标检测;2-图像分类；3-图像分类；4遥感大模型
    private Integer taskType;

    //样本量
    private Long sampleSum;

    //数据集大小, 单位MB
    private Long datasetSize;

    //样本尺寸
    private String sampleSize;

    //分辨率
    private String resolution;

    //图像分类-类别数
    private Integer classes;

    //类别:标注或分类
    private String annotationCategory;

    //标注数量
    private Integer annotationNum;

    //数据字典：标注类型，1-矩形框HBB；2-带角度矩形框OBB；3-像素pixel;4-轮廓
    private Integer annotationType;

    //数据标签，数据字典逗号隔开
    private String tags;

    //波段数
    private Integer bandSize;

    //影像类型
    private String imageType;

    //影像格式
    private String imageForm;

    //影像传感器
    private String instrument;

    //时间范围
    private String timeRange;

    //空间范围
    private String location;

    //数据集路径
    private String obsPath;

    //缩略图
    private String thumbUrl;

    //图片预览
    private String overviewUrl;

    //开源协议
    private String license;

    //相关链接
    private String datasetLink;

    //论文应用方式
    private String datasetCite;

    //数据集模式
    private String datasetMode;

    //数据版本
    private String datasetVersion;

    //是否上线：0下线；1上线
    private Integer isPublic;

    //数据贡献者
    private String datasetCopy;

    //人员信息
    //联系人
    private String contacter;

    //联系电话
    private String phoneNumber;

    //邮箱
    private String email;

    //地址
    private String address;
}
