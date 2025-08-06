package com.zjlab.dataservice.modules.taskplan.model.vo;

import com.zjlab.dataservice.modules.taskplan.model.entity.SatelliteImagingData;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PreplanDataVo {
    private List<PreplanDataPo> preplanDataPos;

    private Integer lowCount;
    private List<SatelliteImagingData> list;

    private  List<String> satelliteIds;

}
