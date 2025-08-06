package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicMapDto;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicJZHPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicTypeVo;

import java.util.List;
@DS("postgre")
public interface ThematicMapService extends IService<ThematicJZHPo> {
    List<ThematicJZHPo> getEntitiesByType(Integer pac, List<ThematicTypeVo> thematicTypeVos);

    PageResult<ThematicJZHPo> getEntitiesByTypePage(Integer pac, ThematicMapDto thematicMapDto, List<ThematicTypeVo> thematicTypeVos);

    List<StatisticsEntity> statistics(Integer pac, List<ThematicTypeVo> thematicTypeVos);

}
