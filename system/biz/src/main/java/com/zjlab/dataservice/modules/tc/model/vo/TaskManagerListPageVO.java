package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 任务列表分页返回 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerListPageVO {
    private List<TaskManagerListItemVO> list;
    private Long total;
}
