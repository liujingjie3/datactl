package com.zjlab.dataservice.modules.taskplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.taskplan.model.dto.TaskAddDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.TaskListDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.TcTaskAddDto;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.vo.TaskManageVo;
import com.zjlab.dataservice.modules.taskplan.model.vo.TaskStaticsVo;
import com.zjlab.dataservice.modules.taskplan.model.vo.taskVo;

import java.util.List;

/**
* @author ZJ
* @description 针对表【task_manage(预规划数据管理表)】的数据库操作Service
* @createDate 2024-08-07 16:05:24
*/
public interface TaskManageService extends IService<TaskManagePo> {

    int addTask(TaskAddDto taskAddDto);

    PageResult<TaskManageVo> qryTaskList(TaskListDto taskListDto);

    TaskStaticsVo statistics();

    List<taskVo> listByStatus(int i);

}
