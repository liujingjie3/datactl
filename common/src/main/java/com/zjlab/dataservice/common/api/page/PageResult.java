package com.zjlab.dataservice.common.api.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    private Integer pageNo;

    private Integer pageSize;

    private Integer total;

    private List<?> data;
}
 
