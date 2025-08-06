package com.zjlab.dataservice.modules.toolbox.model;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class FunctionModule {
    /**
     * 自增id
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
     * 模块开始时间
     */
    private Date startTime;

    /**
     * 模块结束时间
     */
    private Date endTime;

    /**
     * 模块最后编辑时间
     */
    private Date lastModifyTime;

    /**
     * 模块创建人用户id
     */
    private String userId;

    /**
     * 模块路径
     */
    private String modulePath;
}
