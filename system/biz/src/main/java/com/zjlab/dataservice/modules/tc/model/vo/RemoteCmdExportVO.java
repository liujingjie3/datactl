package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 遥控指令单导出VO
 */
@Data
public class RemoteCmdExportVO implements Serializable {


    private static final long serialVersionUID = -317954105992275421L;
    @Excel(name = "序号")
    @NotNull(message = "index不能为空")
    private Integer index;

    @Excel(name = "指令代号")
    @NotBlank(message = "cmdCode不能为空")
    private String cmdCode;

    @Excel(name = "指令名称")
    @NotBlank(message = "cmdName不能为空")
    private String cmdName;

    @Excel(name = "执行时序列")
    @NotBlank(message = "execSequence不能为空")
    private String execSequence;

    @Excel(name = "执行判据")
    @NotBlank(message = "execCriteria不能为空")
    private String execCriteria;

    @Excel(name = "备注")
    private String remark;
}
