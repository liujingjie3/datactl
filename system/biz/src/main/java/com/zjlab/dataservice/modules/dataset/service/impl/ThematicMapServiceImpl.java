package com.zjlab.dataservice.modules.dataset.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.factory.ThematicFactory;
import com.zjlab.dataservice.modules.dataset.mapper.ThematicMapMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicMapDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicQueryDto;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicJZHPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicTypeVo;
import com.zjlab.dataservice.modules.dataset.service.ThematicMapService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
@DS("postgre")
public class ThematicMapServiceImpl extends ServiceImpl<ThematicMapMapper, ThematicJZHPo> implements ThematicMapService {

    @Autowired
    private ThematicMapMapper thematicMapper;

    @Override
    public List<ThematicJZHPo> getEntitiesByType(Integer pac, List<ThematicTypeVo> thematicTypeVos) {
        String tableName = ThematicFactory.getTableNameByType(pac);
        ThematicQueryDto query = ThematicQueryDto.builder().tableName(tableName).pac(pac).build();
        List<ThematicJZHPo> thematics = thematicMapper.queryByCondition(query);
        for (ThematicJZHPo po : thematics) {
            Optional<ThematicTypeVo> result = thematicTypeVos.stream()
                    .filter(obj -> obj.getId().equals(String.valueOf(po.getValue())))
                    .findFirst();
            if (result.isPresent()) {
                po.setBorderColor(result.get().getBorderColor());
                po.setFillColor(result.get().getFillColor());
            }
        }
        return thematics;
    }

    @Override
    public PageResult<ThematicJZHPo> getEntitiesByTypePage(Integer pac, ThematicMapDto thematicMapDto, List<ThematicTypeVo> thematicTypeVos) {
        checkParam(thematicMapDto);
        IPage<ThematicJZHPo> page = new Page<>(thematicMapDto.getPageNo(), thematicMapDto.getPageSize());
        String tableName = ThematicFactory.getTableNameByType(pac);
        ThematicQueryDto query = ThematicQueryDto.builder().tableName(tableName).pac(pac).build();
        IPage<ThematicJZHPo> thematics = baseMapper.queryByConditionPage(page, query);
        List<ThematicJZHPo> listRecords = thematics.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        for (ThematicJZHPo po : listRecords) {
            Optional<ThematicTypeVo> result = thematicTypeVos.stream()
                    .filter(obj -> obj.getId().equals(String.valueOf(po.getValue())))
                    .findFirst();
            if (result.isPresent()) {
                po.setBorderColor(result.get().getBorderColor());
                po.setFillColor(result.get().getFillColor());
            }
        }
        PageResult<ThematicJZHPo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    @Override
    public List<StatisticsEntity> statistics(Integer pac, List<ThematicTypeVo> thematicTypeVos) {
        String tableName = ThematicFactory.getTableNameByType(pac);
        ThematicQueryDto query = ThematicQueryDto.builder().tableName(tableName).pac(pac).build();
        List<StatisticsEntity> areaSumByValue = thematicMapper.getAreaSumByValue(query);
        double totalArea = thematicMapper.getAreaSum(query);
        DecimalFormat df = new DecimalFormat("#.00");
        for (StatisticsEntity entity : areaSumByValue) {
            Optional<ThematicTypeVo> result = thematicTypeVos.stream()
                    .filter(obj -> obj.getId().equals(String.valueOf(entity.getId())))
                    .findFirst();
            entity.setName_cn(result.get().getNameCh());
            entity.setName_en(result.get().getNameEn());
            entity.setPercent(entity.getTotalArea() / totalArea * 100);
            entity.setTotalArea(Double.valueOf(df.format(entity.getTotalArea())));
        }

        return areaSumByValue;
    }

    private void checkParam(ThematicMapDto thematicMapDto) {
        thematicMapDto.setPageNo(Optional.ofNullable(thematicMapDto.getPageNo()).orElse(1));
        thematicMapDto.setPageSize(Optional.ofNullable(thematicMapDto.getPageSize()).orElse(500));
        thematicMapDto.setOrderByType(Optional.ofNullable(thematicMapDto.getOrderByType()).orElse("asc"));
        thematicMapDto.setOrderByField(Optional.ofNullable(thematicMapDto.getOrderByField()).orElse("id"));
    }
}

