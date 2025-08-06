package com.zjlab.dataservice.common.api.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 记录总数
     */
    private Integer total;

}

