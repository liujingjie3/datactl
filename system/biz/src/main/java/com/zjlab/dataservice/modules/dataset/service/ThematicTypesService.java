package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicTypesPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicTypeVo;

import java.util.List;

/**
 * @author ZJ
 * @description 针对表【thematic_types(类别表)】的数据库操作Service
 * @createDate 2024-05-17 09:48:51
 */
public interface ThematicTypesService extends IService<ThematicTypesPo> {
    List<ThematicTypeVo> qryThematicType(Integer dataType);

}
