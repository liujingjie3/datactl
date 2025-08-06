package com.zjlab.dataservice.modules.dataset.model.vo.mark;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MarkExcelDataVo {

    @ExcelProperty("文件名")
    private String fileName;

    @ExcelProperty("文件地址")
    private String fileUrl;

    @ExcelProperty("用户名")
    private String userName;
}
