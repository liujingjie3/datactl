package com.zjlab.dataservice.modules.dataset.model.dto.thematic;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ThematicDataListDto extends PageRequest {
    //搜索框输入条件
    private String query;

    //数据集ID
    private Integer dataSetId;

    private String industry;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeEnd;
}
