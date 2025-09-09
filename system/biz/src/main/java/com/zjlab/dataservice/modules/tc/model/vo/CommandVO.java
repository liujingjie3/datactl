package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

@Data
public class CommandVO {

    private Integer index;

    /**
     * 指令代号
     */
    private String cmdCode;
    /**
     * 指令名称
     */
    private String cmdName;
    /**
     * 时序
     */
    private String execSequence;
    /**
     * 执行判据
     */
    private String execCriteria;
    /**
     * 备注
     */
    private String remark;
}
