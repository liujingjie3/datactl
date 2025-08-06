package com.zjlab.dataservice.modules.taskplan.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.taskplan.model.entity.TaskInfoEntity;
import com.zjlab.dataservice.modules.taskplan.model.po.MetadataGf;
import com.zjlab.dataservice.modules.taskplan.model.vo.MetadataVo;

import java.time.LocalDateTime;

/**
 * @author ZJ
 * @description 针对表【metadata_gf】的数据库操作Service
 * @createDate 2024-08-05 10:18:39
 */
@DS("postgre")
public interface MetadataGfService extends IService<MetadataGf> {

    PageResult<MetadataVo> query(TaskInfoEntity taskInfoEntity);

    MetadataVo queryFile(String geom, LocalDateTime createTime);
}
