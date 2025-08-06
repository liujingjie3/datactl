package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataListDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicEditDto;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicDataPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicListVo;

/**
* @author ZJ
* @description 针对表【thematic_data(专题数据表)】的数据库操作Service
* @createDate 2024-05-17 09:40:32
*/
public interface ThematicDataService extends IService<ThematicDataPo> {

    PageResult<ThematicListVo> qryThematicDataList(ThematicDataListDto thematicDataListDto);

    Integer delThematicData(Integer id);

    String editThematicData(ThematicEditDto thematicEditDto);
}
