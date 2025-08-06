package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataListDto;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicDataPo;
import org.apache.ibatis.annotations.Param;

/**
* @author ZJ
* @description 针对表【thematic_data(专题数据表)】的数据库操作Mapper
* @createDate 2024-05-17 09:40:32
* @Entity generator.domain.ThematicData
*/
public interface ThematicDataMapper extends BaseMapper<ThematicDataPo> {

    IPage<ThematicDataPo> qryThematicDataList(IPage<ThematicDataPo> page, @Param("request") ThematicDataListDto request);


}




