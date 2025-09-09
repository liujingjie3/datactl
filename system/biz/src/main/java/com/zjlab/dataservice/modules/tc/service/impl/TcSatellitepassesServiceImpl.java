package com.zjlab.dataservice.modules.tc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.tc.model.dto.SatellitePassesDto;
import com.zjlab.dataservice.modules.tc.model.entity.TcSatellitepasses;
import com.zjlab.dataservice.modules.tc.service.TcSatellitepassesService;
import com.zjlab.dataservice.modules.tc.mapper.TcSatellitepassesMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TcSatellitepassesServiceImpl extends ServiceImpl<TcSatellitepassesMapper, TcSatellitepasses>
    implements TcSatellitepassesService {

    @Override
    public void addPassInfo(List<SatellitePassesDto> passesList) {
        if (passesList == null || passesList.isEmpty()) {
            return;
        }
        String userId = UserThreadLocal.getUserId();

        // DTO 转 Entity
        List<TcSatellitepasses> entityList = passesList.stream().map(dto -> {
            TcSatellitepasses entity = new TcSatellitepasses();
            entity.setOrbitNo(dto.getOrbitNo());
            entity.setInTime(dto.getInTime());
            entity.setOutTime(dto.getOutTime());
            entity.setSatelliteCode(dto.getSatelliteCode());
            entity.setGroundStation(dto.getGroundStation());
            entity.setCompanyName(dto.getCompanyName());
            entity.setTask(dto.getTask());
            entity.setDuration(dto.getDuration());
            entity.setDelFlag(dto.getDelFlag() != null ? dto.getDelFlag() : false);
            entity.setCreateTime(LocalDateTime.now());
            entity.setCreateBy(userId);
            entity.setUpdateBy(userId);
            entity.setUpdateTime(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());
        // MyBatis-Plus 批量保存
        this.saveBatch(entityList);
    }
}




