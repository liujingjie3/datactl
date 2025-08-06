package com.zjlab.dataservice.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.dataset.aspect.OperationEnum;
import com.zjlab.dataservice.modules.dataset.mapper.OperationLogMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.OperationLogDto;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetOperationLogPo;
import com.zjlab.dataservice.modules.dataset.service.OperationLogService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, DatasetOperationLogPo> implements OperationLogService {

    @Resource
    private ISysUserService userService;

    @Override
    public List<String> operationLog(OperationLogDto operationLogDto) {
        Integer id = operationLogDto.getId();
        String taskType = operationLogDto.getTaskType();
        LambdaQueryWrapper<DatasetOperationLogPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetOperationLogPo::getFileId, id);
        wrapper.eq(DatasetOperationLogPo::getTaskType, taskType);
        wrapper.orderByAsc(DatasetOperationLogPo::getCreateTime);
        List<DatasetOperationLogPo> list = baseMapper.selectList(wrapper);
        List<String> collect = list.stream().map(datasetOperationLogPo -> {
            StringBuilder logSB = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = datasetOperationLogPo.getCreateTime().format(formatter);
            logSB.append(formattedDateTime).append(" ");
            //添加人员信息
            SysUser sysUser = userService.getById(datasetOperationLogPo.getCreateBy());
            if (sysUser == null){

            }else {
                logSB.append(sysUser.getRealname()).append(" ");
            }
            //添加操作信息，
            logSB.append("操作：");
            String operation = datasetOperationLogPo.getOperation();
            if (operation.equals(OperationEnum.CHECK.getName())){
                logSB.append("复查xml,地址").append(datasetOperationLogPo.getMarkUrl());
            }else if (operation.equals(OperationEnum.EDIT.getName())){
                logSB.append(operation);
            }else {
                logSB.append(operation);
            }
            return logSB.toString();
        }).collect(Collectors.toList());
        return collect;
    }
}
