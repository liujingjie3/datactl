package com.zjlab.dataservice.modules.taskplan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.taskplan.model.entity.TaskInfoEntity;
import com.zjlab.dataservice.modules.taskplan.model.po.MetadataGf;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.vo.MetadataVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* @author ZJ
* @description 针对表【metadata_gf】的数据库操作Mapper
* @createDate 2024-08-05 10:18:39
* @Entity generator.domain.MetadataGf
*/
@DS("postgre")
public interface MetadataMapper extends BaseMapper<MetadataGf> {

    IPage<MetadataVo> query(IPage<TaskManagePo> page, @Param("request") TaskInfoEntity taskInfoEntity);

    MetadataVo queryFile(@Param("geom") String geom,@Param("createTime") LocalDateTime createTime);
}




