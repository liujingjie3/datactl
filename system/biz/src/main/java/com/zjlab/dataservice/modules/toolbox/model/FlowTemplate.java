package com.zjlab.dataservice.modules.toolbox.model;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.toolbox.enumerate.TemplateType;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class FlowTemplate {

    /**
     * 自增id
     */
    private int id;

    /**
     * 任务id
     */
    private String templateId;

    /**
     * 任务名
     */
    private String templateName;

    /**
     * 任务状态
     */
    private ToolBoxStatus status;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 任务最后编辑时间
     */
    private Date updateTime;

    /**
     * 模板版本
     */
    private String templateVersion;

    /**
     * 模板内容-JSON String
     */
    private String templateContent;

    /**
     * 创建人id
     */
    private String userId;

    /**
     * 模板类型
     */
    private TemplateType templateType;

    /**
     * 模板缩略图
     */
    private String thumbnail;

}
