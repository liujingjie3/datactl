package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

import com.zjlab.dataservice.modules.tc.model.vo.SatelliteGroupVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 任务创建入参
 */
@Data
public class TaskManagerCreateDto extends TaskManagerBaseDto {


    private static final long serialVersionUID = 8407968386506820592L;
    /** 模板ID */
    @NotBlank(message = "templateId不能为空")
    private String templateId;
    /** 执行卫星分组 */
    private List<SatelliteGroupVO> satellites;
    /** 遥控指令单 */
    private List<RemoteCmdExportVO> remoteCmds;
    /** 轨道计划 */
    private List<OrbitPlanExportVO> orbitPlans;

    /** 执行卫星分组JSON */
    @JsonIgnore
    private String satellitesJson;
    /** 遥控指令单JSON */
    @JsonIgnore
    private String remoteCmdsJson;
    /** 轨道计划JSON */
    @JsonIgnore
    private String orbitPlansJson;

    @Override
    @NotBlank(message = "taskRequirement不能为空")
    public String getTaskRequirement() {
        return super.getTaskRequirement();
    }
}
