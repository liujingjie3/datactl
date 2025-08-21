package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 操作控制项
 */
@Data
public class NodeActionDto implements Serializable {
    private static final long serialVersionUID = 5540144975173155484L;
    /** 操作类型（0=上传，1=选择圈次计划，2=决策，3=文本填写） */
    private Integer type;
    /** 控制项名称 */
    private String name;
    /** 控制项配置 JSON 字符串 */
    private String config;
}
