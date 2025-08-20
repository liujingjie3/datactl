package com.zjlab.dataservice.modules.tc.service.impl;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.modules.tc.mapper.TaskManagerMapper;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.service.TaskManagerService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务相关服务实现
 */
@Service
public class TaskManagerServiceImpl implements TaskManagerService {

    @Resource
    private TaskManagerMapper taskManagerMapper;

    @Override
    public PageResult<TaskManagerListItemVO> listTasks(TaskManagerListQuery query) {
        //todo ,对于管理员看所有任务，因为还没有确认如何判断管理员账号，这里暂时还没有做筛选，就是都能看到所有任务

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

        List<TaskManagerListItemVO> vos = taskManagerMapper.selectTaskList(query);
        long total = taskManagerMapper.countTaskList(query);
        return new PageResult<>(query.getPage(), query.getPageSize(), (int) total, vos);
    }
}
