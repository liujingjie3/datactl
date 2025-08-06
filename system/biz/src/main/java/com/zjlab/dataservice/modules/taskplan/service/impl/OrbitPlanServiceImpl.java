package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.service.OrbitPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link OrbitPlanService}.
 * 实际的轨道计划生成逻辑需根据业务补充。
 */
@Service
@Slf4j
public class OrbitPlanServiceImpl implements OrbitPlanService {

    @Override
    public void generateOrbitPlan(TaskManagePo task, List<String> commands) {
        log.info("为任务{}生成轨道计划, 指令数量:{}", task.getTaskName(), commands.size());
        // TODO 实现真实的轨道计划生成逻辑
    }
}
