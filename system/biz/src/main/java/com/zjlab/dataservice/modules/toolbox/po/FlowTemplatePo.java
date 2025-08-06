package com.zjlab.dataservice.modules.toolbox.po;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import com.zjlab.dataservice.modules.toolbox.enumerate.TemplateType;
import com.zjlab.dataservice.modules.toolbox.enumerate.ToolBoxStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("flow_template")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FlowTemplatePo {

    /**
     * id
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
     * 任务开始时间
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
     * 模板内容
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
