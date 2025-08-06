package com.zjlab.dataservice.modules.subject.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.zjlab.dataservice.modules.subject.entity.SubjectWorkflow;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 工作流表
 * @Author: jeecg-boot
 * @Date:   2023-12-05
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="workflowVO", description="workflowVO")
public class SubjectWorkflowVO extends SubjectWorkflow implements Serializable {

	/**工作流参数*/
	@Excel(name = "工作流参数", width = 15)
    @ApiModelProperty(value = "工作流参数")
    private JSONObject flowDataVo;
}
