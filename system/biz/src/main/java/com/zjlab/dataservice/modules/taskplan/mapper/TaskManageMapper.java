package com.zjlab.dataservice.modules.taskplan.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.StaticsVo;
import com.zjlab.dataservice.modules.taskplan.model.dto.TaskListDto;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.taskplan.model.vo.TaskStaticsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ZJ
 * @description 针对表【task_manage(预规划数据管理表)】的数据库操作Mapper
 * @createDate 2024-08-07 16:05:24
 * @Entity com.zjlab.dataservice.modules.taskplan.model.po.TaskManage
 */
public interface TaskManageMapper extends BaseMapper<TaskManagePo> {

    IPage<TaskManagePo> qryTaskList(IPage<TaskManagePo> page, @Param("request") TaskListDto taskListDto);

    TaskStaticsVo statistics(@Param("userId") String userId);

    List<TaskManagePo> selectByStatus(@Param("status") int status);

}




