package com.zjlab.dataservice.modules.task.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.modules.task.enums.TaskStatusEnum;
import com.zjlab.dataservice.modules.task.mapper.TaskMapper;
import com.zjlab.dataservice.modules.task.model.dto.TaskListQuery;
import com.zjlab.dataservice.modules.task.model.po.TaskListItemPO;
import com.zjlab.dataservice.modules.task.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.task.model.vo.TaskListItemVO;
import com.zjlab.dataservice.modules.task.model.vo.TaskListPageVO;
import com.zjlab.dataservice.modules.task.model.vo.TemplateVO;
import com.zjlab.dataservice.modules.task.service.TaskService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务相关服务实现
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskMapper taskMapper;

    @Override
    public TaskListPageVO listTasks(TaskListQuery query) {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof LoginUser) {
            query.setUserId(((LoginUser) principal).getId());
        }

        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setOffset((query.getPage() - 1) * query.getPageSize());

        List<TaskListItemPO> pos = taskMapper.selectTaskList(query);
        List<TaskListItemVO> vos = new ArrayList<>();
        for (TaskListItemPO po : pos) {
            TaskListItemVO vo = new TaskListItemVO();
            vo.setTaskId(po.getTaskId());
            vo.setTaskName(po.getTaskName());
            vo.setTaskCode(po.getTaskCode());
            if (po.getTemplate() != null) {
                vo.setTemplate(JSON.parseObject(po.getTemplate(), TemplateVO.class));
            }
            vo.setSatellites(po.getSatellites());
            vo.setCreateTime(po.getCreateTime());
            vo.setStatus(TaskStatusEnum.fromCode(po.getStatus()));
            if (po.getCurrentNodes() != null) {
                List<CurrentNodeVO> nodes = JSON.parseArray(po.getCurrentNodes(), CurrentNodeVO.class);
                vo.setCurrentNodes(nodes);
            }
            vos.add(vo);
        }
        long total = taskMapper.countTaskList(query);
        return new TaskListPageVO(vos, total);
    }
}
