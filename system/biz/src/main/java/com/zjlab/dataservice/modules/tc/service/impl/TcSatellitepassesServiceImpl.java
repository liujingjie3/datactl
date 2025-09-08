package com.zjlab.dataservice.modules.tc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.tc.model.dto.SatellitePassesDto;
import com.zjlab.dataservice.modules.tc.model.entity.TcSatellitepasses;
import com.zjlab.dataservice.modules.tc.service.TcSatellitepassesService;
import com.zjlab.dataservice.modules.tc.mapper.xml.TcSatellitepassesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcSatellitepassesServiceImpl extends ServiceImpl<TcSatellitepassesMapper, TcSatellitepasses>
    implements TcSatellitepassesService {

    @Override
    public void addPassInfo(List<SatellitePassesDto> passesList) {

    }
}




