package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.SatelliteGroupVO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 任务创建入参
 */
@Data
public class PassInfoQueryDto {

    /**
     * 执行卫星分组
     */
    @NotEmpty(message = "satellites不能为空")
    @Valid
    private List<SatelliteGroupVO> satellites;

}
