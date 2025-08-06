package com.zjlab.dataservice.modules.tc.event;

public interface TcEventType {

    String TC_INSTANCE_CREATE = "tc_instance_create";

    String TC_INSTANCE_UPDATE_STATUS = "tc_instance_update_status";

    /**
     * 流程结束
     */
    String TC_INSTANCE_COMPLETE = "tc_instance_complete";

    String TC_TASK_CREATE = "tc_task_create";

    String TC_TASK_UPDATE_STATUS = "tc_task_update_status";

    /**
     * 同意
     */
    String TC_TASK_AGREE = "tc_task_agree";

    /**
     * 拒绝
     */
    String TC_TASK_REFUSED = "tc_task_refused";

    String TC_READ_CREATE = "tc_read_create";

    String TC_READ_UPDATE_STATUS = "tc_read_update_status";

}
