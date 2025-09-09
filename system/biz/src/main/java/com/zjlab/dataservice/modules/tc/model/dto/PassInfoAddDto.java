package com.zjlab.dataservice.modules.tc.model.dto;

import com.zjlab.dataservice.modules.tc.model.vo.SatelliteGroupVO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 任务创建入参
 */
@Data
public class PassInfoAddDto {

    /**
     * 卫星轨迹信息
     */
    private List<SatellitePassesDto> satellitePassesDtoList;

}
