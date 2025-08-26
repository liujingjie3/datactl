package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 卫星分组信息
 */
@Data
public class SatelliteGroupVO implements Serializable {

    private static final long serialVersionUID = 806291365827527820L;
    /** 星座或分组名称 */
    private String group;
    /** 分组下的卫星ID集合 */
    private List<String> satIds;
}
