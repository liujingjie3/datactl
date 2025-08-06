package com.zjlab.dataservice.modules.backup.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.util.TimeUtils;
import com.zjlab.dataservice.modules.backup.mapper.TaskRecordMapper;
import com.zjlab.dataservice.modules.backup.model.dto.TaskRecordDto;
import com.zjlab.dataservice.modules.backup.model.entity.TaskRecordListEntity;
import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
import com.zjlab.dataservice.modules.backup.model.vo.TaskRecordVo;
import com.zjlab.dataservice.modules.backup.service.TaskRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskRecordServiceImpl extends ServiceImpl<TaskRecordMapper, TaskRecordPo> implements TaskRecordService {


    @Override
    public PageResult<TaskRecordVo> qryTaskRecordList(TaskRecordDto recordDto) {
        checkParam(recordDto);
        //整理查询参数
        IPage<TaskRecordListEntity> page = new Page<>(recordDto.getPageNo(), recordDto.getPageSize());
        TaskRecordListEntity entity = new TaskRecordListEntity();
        BeanUtils.copyProperties(recordDto, entity);
        LocalDateTime beforeDays = TimeUtils.getBeforeDays(recordDto.getExpireDay());
        entity.setExpireDay(beforeDays);
        //查询结果
        IPage<TaskRecordListEntity> recordList = baseMapper.qryTaskRecordList(page, entity);
        List<TaskRecordListEntity> listRecords = recordList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)){
            return new PageResult<>();
        }
        List<TaskRecordVo> collect = listRecords.stream().map(recordEntity -> {
            TaskRecordVo recordVo = new TaskRecordVo();
            BeanUtils.copyProperties(recordEntity, recordVo);
            return recordVo;
        }).collect(Collectors.toList());
        //整理结果返回
        PageResult<TaskRecordVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    private void checkParam(TaskRecordDto recordDto) {
        recordDto.setPageNo(Optional.ofNullable(recordDto.getPageNo()).orElse(1));
        recordDto.setPageSize(Optional.ofNullable(recordDto.getPageSize()).orElse(10));
        String field = recordDto.getOrderByField();
        if (StringUtils.isBlank(field)){
            recordDto.setOrderByField("update_time");
        }else {
            //驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")){
                result = result.substring(1);
            }
            recordDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(recordDto.getOrderByType())){
            recordDto.setOrderByType("desc");
        }
    }
}
