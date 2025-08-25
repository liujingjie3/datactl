package com.zjlab.dataservice.modules.tc.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 节点操作记录
 */
@Data
@TableName("tc_task_node_action_record")
public class TaskNodeActionRecord extends TcBaseEntity {

    private static final long serialVersionUID = 1531697776856485836L;
    /** 任务ID */
    private Long taskId;
    /** 节点实例ID */
    private Long nodeInstId;
    /** 操作类型(0上传,1选择计划,2决策,3文本等) */
    private Integer actionType;
    /** 提交内容JSON */
    private String actionPayload;
}
