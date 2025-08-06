package com.zjlab.dataservice.modules.tc.common;

public interface Constants {

    /**
     *
     实例创建事件
     */
    String TC_INSTANCE_CREATE = "tc_instance_create";

    /**
     * 任务催办事件
     */
    String TC_TASK_URGE = "tc_task_urge";

    /**
     * flag 状态取值
     * 9 查询全部， 1完成, 0运行中 -1驳回
     */
    Integer FLAG_ALL = 9;

    Integer FLAG_COMPLETE = 1;
    Integer FLAG_RUNNING = 0;
    Integer FLAG_BACK = -1;

    /**
     * BPM待办提醒
     */
    String BPM_TASK = "9001";
    /**
     * BPM审批通过通知
     */
    String BPM_AUDIT_SUCCESS = "9002";
    /**
     * BPM审批驳回通知
     */
    String BPM_AUDIT_BACK = "9003";
    /**
     * BPM审批催办
     */
    String BPM_URGE_TASK = "9004";
    /**
     * BPM流程结束
     */
    String BPM_INSTANCE_END = "9005";
    /**
     * BPM传阅通知
     */
    String BPM_READ = "9006";

}
