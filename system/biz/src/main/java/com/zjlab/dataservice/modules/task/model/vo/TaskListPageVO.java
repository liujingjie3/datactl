package com.zjlab.dataservice.modules.task.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 任务列表分页返回 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListPageVO {
    private List<TaskListItemVO> list;
    private Long total;
}
