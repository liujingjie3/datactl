package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.service.RemoteCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link RemoteCommandService}.
 * 当前实现仅作为占位，实际匹配逻辑需结合业务完善。
 */
@Service
@Slf4j
public class RemoteCommandServiceImpl implements RemoteCommandService {

    @Override
    public List<String> matchCommands(TaskManagePo task) {
        log.info("匹配任务{}的遥控指令", task.getTaskName());
        // TODO 实现真实的遥控指令匹配逻辑
        return Collections.emptyList();
    }
}
