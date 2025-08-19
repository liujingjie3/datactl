package com.zjlab.dataservice.modules.task.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 查询任务列表的参数
 */
@Data
public class TaskListQuery {
    /** Tab类型：all/startedByMe/todo/participated/handled */
    private String tab;
    /** 模糊搜索：任务名称或编码 */
    private String q;
    /** 任务状态 */
    private Integer status;
    /** 模板ID */
    private Long templateId;
    /** 创建时间起 */
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
