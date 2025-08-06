package com.zjlab.dataservice.modules.taskplan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import com.zjlab.dataservice.modules.taskplan.model.dto.PreplanDataDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.QueryCenterDto;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.taskplan.model.vo.QueryDateVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author ZJ
* @description 针对表【preplan_data】的数据库操作Mapper
* @createDate 2024-07-22 16:59:10
* @Entity com.zjlab.dataservice.modules.taskplan.model.po.PreplanData
*/
@DS("postgre")
public interface PreplanDataMapper extends BaseMapper<PreplanDataPo> {
    List<PreplanDataPo> query(@Param("request")PreplanDataDto preplanDataDto);

    List<PreplanDataPo> queryCenter(@Param("request")QueryCenterDto queryDto);

    QueryDateVo queryDate();

    List<String> queryExistingSatelliteIds(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
}




