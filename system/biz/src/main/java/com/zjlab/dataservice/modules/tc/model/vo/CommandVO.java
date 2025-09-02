package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

@Data
public class CommandVO {
    private Integer id;


    /**
     * 模版ID
     */
    private String templateId;
    /**
     * 指令代号
     */
    private String code;
    /**
     * 指令名称
     */
    private String name;
    /**
     * 时序
     */
    private String timeOrder;
    /**
     * 执行判据
     */
    private String execution;
    /**
     * 备注
     */
    private String description;
    /**
     * 删除标记（0=正常，1=删除）
     */

}
