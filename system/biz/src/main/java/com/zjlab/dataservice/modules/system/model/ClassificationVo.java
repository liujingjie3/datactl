package com.zjlab.dataservice.modules.system.model;

import lombok.Builder;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@Builder
public class ClassificationVo {

    /**
     * 字典项文本
     */
    @Excel(name = "字典项文本", width = 20)
    private String classficationName;

    /**
     * 字典项值
     */
    @Excel(name = "字典项值", width = 30)
    private String classficationValue;


    @Excel(name = "描述", width = 40)
    private String description;

    /**
     * 排序
     */
    @Excel(name = "排序", width = 15,type=4)
    private Integer sortOrder;

}
