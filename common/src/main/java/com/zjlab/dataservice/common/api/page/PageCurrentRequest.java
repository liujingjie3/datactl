package com.zjlab.dataservice.common.api.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageCurrentRequest {
    /**
     * 当前页码
     */
    private Long pageIndex;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 排序字段
     */
    private String orderByField;

    /**
     * 排序direction
     */
    private String orderByType;

}
 
