package com.zjlab.dataservice.modules.tc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.common.CommonUtil;
import com.zjlab.dataservice.modules.tc.mapper.TodoEventFailMapper;
import com.zjlab.dataservice.modules.tc.model.dto.TodoEventFailDto;
import com.zjlab.dataservice.modules.tc.model.entity.TodoEventFail;
import com.zjlab.dataservice.modules.tc.service.TcEventFailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TcEventFailServiceImpl extends ServiceImpl<TodoEventFailMapper, TodoEventFail> implements
        TcEventFailService {

    @Override
    public PageResult<TodoEventFailDto> selectTodoEventFailPage(TodoEventFailDto failDto) {
        // 参数校验
        CommonUtil.checkPageAndOrderParam(failDto);
        IPage<TodoEventFailDto> page = new Page<>(failDto.getPageNo(), failDto.getPageSize());

        // 查询数据
        IPage<TodoEventFailDto> resultPage = baseMapper.selectTodoEventFailPage(page, failDto);

        List<TodoEventFailDto> listRecords = resultPage.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        PageResult<TodoEventFailDto> result = CommonUtil.buildPageResult(page, listRecords);
        return result;
    }

}
