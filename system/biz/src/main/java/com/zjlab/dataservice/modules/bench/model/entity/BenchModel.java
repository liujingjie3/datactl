package com.zjlab.dataservice.modules.bench.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 模型表（bench_models）
 * 包含模型综合得分及各能力维度得分
 */
@Data
@TableName("bench_models")
public class BenchModel  extends BasePo  {

    /** 模型名称 */
    private String name;

    /** 团队ID（外键关联 bench_teams） */
    private Integer teamId;

    /** 发布日期 */
    private Date releaseDate;

    /** 开闭源类型（枚举值：开源/闭源） */
    private String sourceType;

    /** 模型类型 */
    private String modelType;

    /** 参数量 */
    private String parameters;

    /** 综合得分 */
    private Float overallScore;

    /** 粗粒度视觉感知能力 */
    private Float globalVisualPerception;

    /** 细粒度视觉感知能力 */
    private Float fineVisualPerception;

    /** 视觉问答能力 */
    private Float visualQuestionAnswering;

    /** 语言生成能力 */
    private Float languageGeneration;

    /** 时空分析与推理能力 */
    private Float spatiotemporalReasoning;

    /** 有效数据集评分数量 */
    private Integer count;

    // 15项任务得分
    private Float imgCls;  // 图像分类
    private Float regClsHbb;   //水平框区域HBB
    private Float regClsObb;  // 旋转框区域OBB
    private Float pixCls;  //多边形区域
    private Float regDetHbb;  //水平框目标HBB
    private Float regDetObb;  //旋转框目标OBB
    private Float regVg;  //视觉定位
    private Float pixSeg;  //目标分割
    private Float imgCap;  //图像简短描述
    private Float imgCapDetailed;  // 详细图像描述
    private Float regCap;  // 区域描述
    private Float vqaPresence;  // 视觉问答-存在性
    private Float vqaNumcomp;  // 视觉问答-数值比较
    private Float pixChg;  // 像素变化
    private Float imgCt;  // 目标计数

}
