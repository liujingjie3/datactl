package com.zjlab.dataservice.modules.dataset.model.vo.mark;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MarkListVo {

    private Integer pageNo;

    private Integer pageSize;

    private Integer total;

    private List<MarkFileDetailVo> data;
}
