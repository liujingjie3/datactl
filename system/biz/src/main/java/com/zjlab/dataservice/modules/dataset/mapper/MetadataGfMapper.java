package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.entity.MonthlyPeriodEntity;
import com.zjlab.dataservice.modules.dataset.model.po.MetadataGfPo;
import com.zjlab.dataservice.modules.myspace.model.entity.ImageSelectEntity;
import com.zjlab.dataservice.modules.dataset.model.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("postgre")
public interface MetadataGfMapper extends BaseMapper<MetadataGfPo> {

    List<MonthlyPeriodEntity> qryMonthlyList();

    IPage<MetadataGfPo> selectImageList(IPage<MetadataGfPo> page, @Param("entity") ImageSelectEntity entity);

    IPage<MetadataGfPo> qryMetadataList(IPage<MetadataGfPo> page, @Param("request") MetadataListEntity request);

    int filterMetadata(@Param("entity") MetadataFilterEntity entity);

    int selectExistIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    int assignMetadata(@Param("entity") AssignMetadataEntity entity);

}
