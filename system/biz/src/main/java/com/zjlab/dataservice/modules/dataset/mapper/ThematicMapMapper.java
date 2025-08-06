package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataListDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicQueryDto;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicDataPo;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicJZHPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DS("postgre")
public interface ThematicMapMapper extends BaseMapper<ThematicJZHPo> {

    List<ThematicJZHPo> queryByCondition(@Param("request") ThematicQueryDto entity);

    List<StatisticsEntity> getAreaSumByValue(@Param("request") ThematicQueryDto query);

    double getAreaSum(@Param("request" ) ThematicQueryDto query);

    IPage<ThematicJZHPo> queryByConditionPage(IPage<ThematicJZHPo> page, @Param("request") ThematicQueryDto entity);

}
