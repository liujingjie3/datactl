package com.zjlab.dataservice.modules.toolbox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.toolbox.dto.TaskDto;
import com.zjlab.dataservice.modules.toolbox.dto.ToolBoxDto;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import com.zjlab.dataservice.modules.toolbox.po.TaskPo;
import com.zjlab.dataservice.modules.toolbox.vo.TaskVo;

import java.util.List;

public interface ToolboxTaskService extends IService<TaskPo> {
    // listAllByUser

    PageResult<TaskVo> listTaskByCondition(ToolBoxDto toolBoxDto);

    TaskVo queryTask(String taskId);

    // create - step 1,2,3
    TaskVo createTask(TaskDto taskDto);

    // delete by id
    boolean deleteTaskById(String taskId);

    TaskVo updateTaskById(TaskDto taskDto);

    // start -
    TaskVo startTask(TaskDto taskDto);

    // stop -
    TaskVo stopTask(int id);

    // retry -
    TaskVo retryTask(int id);


    List<TaskPo> queryTasksByStatus(ToolBoxStatus toolBoxStatus);
}
