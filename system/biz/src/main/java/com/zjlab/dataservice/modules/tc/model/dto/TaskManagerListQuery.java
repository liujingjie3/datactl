package com.zjlab.dataservice.modules.tc.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询任务列表的参数
 */
@Data
public class TaskManagerListQuery implements Serializable {
    private static final long serialVersionUID = 73115360324038625L;
    /** Tab类型：all/startedByMe/todo/handled/participated */
    @NotBlank(message = "tab不能为空")
    @Pattern(regexp = "all|startedByMe|todo|handled|participated", message = "tab必须为all/startedByMe/todo/handled/participated")
    private String tab;
    /** 模糊搜索：任务名称或编码 */
    private String q;
    /** 任务状态 */
    private Integer status;
    /** 模板ID */
    private String templateId;
    /** 创建时间起 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeFrom;
    /** 页码，默认1 */
    private Integer page;
    /** 每页条数，默认10 */
    private Integer pageSize;
    /** 当前登录用户ID */
    private String userId;
    /** 偏移量，内部计算 */
    private Integer offset;
}
