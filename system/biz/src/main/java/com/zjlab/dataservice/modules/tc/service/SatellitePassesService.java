package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.modules.tc.model.dto.PassInfoQueryDto;
import com.zjlab.dataservice.modules.tc.model.entity.SatellitePasses;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.tc.model.vo.SatellitePassesVO;

import java.util.List;

public interface SatellitePassesService extends IService<SatellitePasses> {

    List<SatellitePassesVO> passinfo(PassInfoQueryDto dto);
}
