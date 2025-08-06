package com.zjlab.dataservice.modules.taskplan.service;

import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import java.util.List;

/**
 * Service for generating orbit plans based on tasks and matched commands.
 */
public interface OrbitPlanService {
    /**
     * 根据任务及遥控指令生成轨道计划
     *
     * @param task     任务信息
     * @param commands 遥控指令
     */
    void generateOrbitPlan(TaskManagePo task, List<String> commands);
}
