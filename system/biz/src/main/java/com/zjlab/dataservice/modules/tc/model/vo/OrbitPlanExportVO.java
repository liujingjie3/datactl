package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 轨道计划导出VO
 */
@Data
public class OrbitPlanExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "飞控圈次")
    private String orbitNo;

    @Excel(name = "进站时间")
    private String inTime;

    @Excel(name = "出站时间")
    private String outTime;

    @Excel(name = "卫星代号")
    private String satelliteCode;

    @Excel(name = "地面站")
    private String groundStation;

    @Excel(name = "公司名称")
    private String companyName;

    @Excel(name = "时长")
    private String duration;

    @Excel(name = "执行任务")
    private String task;

    @Excel(name = "圈次是否使用")
    private String used;
}
