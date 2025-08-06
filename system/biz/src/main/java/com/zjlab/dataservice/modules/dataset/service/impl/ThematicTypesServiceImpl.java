package com.zjlab.dataservice.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.system.query.DataTypeEnum;
import com.zjlab.dataservice.common.system.query.GeoTypeEnum;
import com.zjlab.dataservice.modules.dataset.mapper.ThematicTypesMapper;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicTypesPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicTypeVo;
import com.zjlab.dataservice.modules.dataset.service.ThematicTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【thematic_types(类别表)】的数据库操作Service实现
 * @createDate 2024-05-17 09:48:51
 */
@Service
public class ThematicTypesServiceImpl extends ServiceImpl<ThematicTypesMapper, ThematicTypesPo>
        implements ThematicTypesService {
    @Autowired
    private ThematicTypesMapper thematicTypesMapper;

    @Override
    public List<ThematicTypeVo> qryThematicType(Integer dataType) {
        DataTypeEnum dataTypeEnum = DataTypeEnum.fromType(dataType);
        LambdaQueryWrapper<ThematicTypesPo> query = new LambdaQueryWrapper<>();
        query.eq(ThematicTypesPo::getParent, dataTypeEnum.getValue());
        List<ThematicTypesPo> list = thematicTypesMapper.selectList(query);
        List<ThematicTypeVo> collec = list.stream().map(ThematicTypesPo -> {
            ThematicTypeVo thematicTypeVo = ThematicTypeVo.builder()
                    .id(GeoTypeEnum.getDescriptionByCode(ThematicTypesPo.getId()))
                    .nameCh(ThematicTypesPo.getNameCh())
                    .nameEn(ThematicTypesPo.getNameEn())
                    .parent(ThematicTypesPo.getParent())
                    .description(ThematicTypesPo.getDescription())
                    .icon(ThematicTypesPo.getIcon())
                    .background(ThematicTypesPo.getBackground())
                    .maxRes(ThematicTypesPo.getMaxRes())
                    .minRes(ThematicTypesPo.getMinRes())
                    .bestRes(ThematicTypesPo.getBestRes())
                    .bandCount(ThematicTypesPo.getBandCount())
                    .inputCount(ThematicTypesPo.getInputCount())
                    .largeImage(ThematicTypesPo.getLargeImage())
                    .fillColor(ThematicTypesPo.getFillColor())
                    .borderColor(ThematicTypesPo.getBorderColor())
                    .build();
            return thematicTypeVo;
        }).collect(Collectors.toList());
        return collec;
    }
}




