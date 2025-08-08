package com.zjlab.dataservice.modules.tc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeStatsVo {
    private Integer activeCount;
    private Integer disabledCount;
    private Integer totalCount;
}
