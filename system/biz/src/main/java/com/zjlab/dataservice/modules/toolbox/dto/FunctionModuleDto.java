package com.zjlab.dataservice.modules.toolbox.dto;

import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;

import java.util.Date;

public class FunctionModuleDto {
    /**
     * id
     */
    private int id;

    /**
     * 模块id
     */
    private String moduleId;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 模块状态
     */
    private ToolBoxStatus status;

    /**
     * 模块描述
     */
    private String description;

    /**
     * 模块创建时间
     */
    private Date createTime;

    /**
     * 模块更新时间
     */
    private Date updateTime;

    /**
     * 模块创建人用户id
     */
    private String userId;

    /**
     * 模块路径
     */
    private String modulePath;
}
