package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 遥控指令单导出VO
 */
@Data
public class RemoteCmdExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "序号")
    private Integer index;

    @Excel(name = "指令代号")
    private String cmdCode;

    @Excel(name = "指令名称")
    private String cmdName;

    @Excel(name = "执行时序列")
    private String execSequence;

    @Excel(name = "执行判据")
    private String execCriteria;

    @Excel(name = "备注")
    private String remark;
}
