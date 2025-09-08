package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.modules.tc.model.dto.SatellitePassesDto;
import com.zjlab.dataservice.modules.tc.model.entity.TcSatellitepasses;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TcSatellitepassesService extends IService<TcSatellitepasses> {

    void addPassInfo(List<SatellitePassesDto> passesList);
}
