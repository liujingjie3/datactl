package com.zjlab.dataservice.modules.storage.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.JeecgEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@TableName("storage_hdfs_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FilePo extends JeecgEntity {

    private static final long serialVersionUID = 1L;

    @Excel(name = "文件名称")
    private String fileName;

    @Excel(name = "文件地址")
    private String url;

    private String md5;
}
