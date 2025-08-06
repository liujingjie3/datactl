package com.zjlab.dataservice.modules.tc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.common.CommonUtil;
import com.zjlab.dataservice.modules.tc.mapper.FlowTypeMapper;
import com.zjlab.dataservice.modules.tc.mapper.TodoEventMapper;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTaskDto;
import com.zjlab.dataservice.modules.tc.model.dto.TodoTaskQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.FlowType;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEvent;
import com.zjlab.dataservice.modules.tc.service.TcEventService;
import com.zjlab.dataservice.modules.tc.service.TcFlowTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcEventServiceImpl extends ServiceImpl<TodoEventMapper, TodoEvent> implements
        TcEventService {

    @Override
    public PageResult<TodoEventDto> selectTodoEventPage(TodoEventDto eventDto) {
        // 参数校验
        CommonUtil.checkPageAndOrderParam(eventDto);
        IPage<TodoEventDto> page = new Page<>(eventDto.getPageNo(), eventDto.getPageSize());

        // 查询数据
        IPage<TodoEventDto> resultPage = baseMapper.selectTodoEventPage(page, eventDto);

        List<TodoEventDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TodoEventDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;
    }

}
