package com.zjlab.dataservice.modules.taskplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjlab.dataservice.modules.taskplan.model.dto.PreplanDataDto;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import com.zjlab.dataservice.modules.taskplan.model.vo.PreplanDataVo;
import com.zjlab.dataservice.modules.taskplan.model.vo.QueryDateVo;

/**
* @author ZJ
* @description 针对表【preplan_data】的数据库操作Service
* @createDate 2024-07-22 16:59:10
*/
public interface PreplanDataService extends IService<PreplanDataPo> {

    PreplanDataVo query(PreplanDataDto preplanDataDto) throws JsonProcessingException;

    QueryDateVo queryDate();
}
