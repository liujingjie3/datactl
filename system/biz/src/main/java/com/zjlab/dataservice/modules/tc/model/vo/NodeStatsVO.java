package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeStatsVO implements Serializable {
    private static final long serialVersionUID = -2910543287558173917L;
    private Integer activeCount;
    private Integer disabledCount;
    private Integer totalCount;
}
