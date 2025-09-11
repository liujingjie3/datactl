package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 轨道计划导出VO
 */
@Data
public class OrbitPlanExportVO implements Serializable {

    private static final long serialVersionUID = 3886975097530256873L;
    @Excel(name = "飞控圈次")
    @NotBlank(message = "orbitNo不能为空")
    private String orbitNo;

    @Excel(name = "进站时间")
    @NotBlank(message = "inTime不能为空")
    private String inTime;

    @Excel(name = "出站时间")
    @NotBlank(message = "outTime不能为空")
    private String outTime;

    @Excel(name = "卫星代号")
    @NotBlank(message = "satelliteCode不能为空")
    private String satelliteCode;

    @Excel(name = "地面站")
    @NotBlank(message = "groundStation不能为空")
    private String groundStation;

    @Excel(name = "公司名称")
    @NotBlank(message = "companyName不能为空")
    private String companyName;

    @Excel(name = "时长")
    @NotBlank(message = "duration不能为空")
    private String duration;

    @Excel(name = "执行任务")
    @NotBlank(message = "task不能为空")
    private String task;

    @Excel(name = "圈次是否使用")
    @NotNull(message = "used不能为空")
    private Boolean used;
}
