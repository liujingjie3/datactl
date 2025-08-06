package com.zjlab.dataservice.modules.taskplan.service;

import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import java.util.List;

/**
 * Service for matching remote control commands based on task information.
 */
public interface RemoteCommandService {
    /**
     * 根据任务信息自动匹配遥控指令单
     *
     * @param task 任务信息
     * @return 匹配到的遥控指令列表
     */
    List<String> matchCommands(TaskManagePo task);
}
