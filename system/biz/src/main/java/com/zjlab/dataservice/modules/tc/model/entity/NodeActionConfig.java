package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 操作控制项配置表
 */
@TableName("node_action_config")
@Data
public class NodeActionConfig extends TcBaseEntity {
    /** 操作类型（0=上传，1=选择圈次计划，2=决策，3=文本填写） */
    private Integer type;
    /** 操作项名称 */
    private String name;
    /** 配置信息 JSON 字符串 */
    private String config;
}

