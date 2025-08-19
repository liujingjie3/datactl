package com.zjlab.dataservice.modules.tc.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import com.zjlab.dataservice.modules.tc.enums.TaskManagerStatusEnum;
import com.zjlab.dataservice.modules.tc.mapper.TaskManagerMapper;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.po.TaskManagerListItemPO;
import com.zjlab.dataservice.modules.tc.model.vo.CurrentNodeVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListPageVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateVO;
import com.zjlab.dataservice.modules.tc.service.TaskManagerService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务相关服务实现
 */
@Service
public class TaskManagerServiceImpl implements TaskManagerService {

    @Resource
    private TaskManagerMapper taskManagerMapper;

    @Override
    public TaskManagerListPageVO listTasks(TaskManagerListQuery query) {
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

        List<TaskManagerListItemPO> pos = taskManagerMapper.selectTaskList(query);
        List<TaskManagerListItemVO> vos = new ArrayList<>();
        for (TaskManagerListItemPO po : pos) {
            TaskManagerListItemVO vo = new TaskManagerListItemVO();
            vo.setTaskId(po.getTaskId());
            vo.setTaskName(po.getTaskName());
            vo.setTaskCode(po.getTaskCode());
            if (po.getTemplate() != null) {
                vo.setTemplate(JSON.parseObject(po.getTemplate(), TemplateVO.class));
            }
            vo.setSatellites(po.getSatellites());
            vo.setCreateTime(po.getCreateTime());
            vo.setStatus(TaskManagerStatusEnum.fromCode(po.getStatus()));
            if (po.getCurrentNodes() != null) {
                List<CurrentNodeVO> nodes = JSON.parseArray(po.getCurrentNodes(), CurrentNodeVO.class);
                vo.setCurrentNodes(nodes);
            }
            vos.add(vo);
        }
        long total = taskManagerMapper.countTaskList(query);
        return new TaskManagerListPageVO(vos, total);
    }
}
