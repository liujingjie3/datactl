package com.zjlab.dataservice.modules.myspace.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BaseIntPo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("my_favourite")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CollectPo extends BaseIntPo {

    private String userId;

    private String fileName;

    private Float fileSize;

    private String path;

    private String parentPath;

    private Integer isDir;

    private Integer imageId;

    private String imageType;

    private Integer applyStatus;

    private String fileUrl;

    private String thumbUrl;

    private Float imageGsd;
}
