package com.zjlab.dataservice.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.modules.dataset.mapper.ThematicDataMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataListDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicEditDto;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicDataPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicListVo;
import com.zjlab.dataservice.modules.dataset.service.ThematicDataService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【thematic_data(专题数据表)】的数据库操作Service实现
 * @createDate 2024-05-17 09:40:32
 */
@Service
public class ThematicDataServiceImpl extends ServiceImpl<ThematicDataMapper, ThematicDataPo>
        implements ThematicDataService {

    @Override
    public PageResult<ThematicListVo> qryThematicDataList(ThematicDataListDto thematicDataListDto) {
        checkParam(thematicDataListDto);

        IPage<ThematicDataPo> page = new Page<>(thematicDataListDto.getPageNo(), thematicDataListDto.getPageSize());
        IPage<ThematicDataPo> datasetList = baseMapper.qryThematicDataList(page, thematicDataListDto);
        List<ThematicDataPo> listRecords = datasetList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }

        List<ThematicListVo> collect = listRecords.stream().map(ThematicDataPo -> {
            ThematicListVo thematicListVo = ThematicListVo.builder()
                    .id(ThematicDataPo.getId())
                    .name(ThematicDataPo.getName())
                    .province(ThematicDataPo.getProvince())
                    .pac(ThematicDataPo.getPac())
                    .productType(ThematicDataPo.getProductType())
                    .format(ThematicDataPo.getFormat())
                    .dataSource(ThematicDataPo.getDataSource())
                    .isCoords(ThematicDataPo.getIsCoords())
                    .thumb(ThematicDataPo.getThumb())
                    .regionName(ThematicDataPo.getRegionName())
                    .industry(ThematicDataPo.getIndustry())
                    .introduction(ThematicDataPo.getIntroduction())
                    .keywords(ThematicDataPo.getKeywords())
                    .createTime(ThematicDataPo.getCreateTime())
                    .createBy(ThematicDataPo.getCreateBy())
                    .updateBy(ThematicDataPo.getUpdateBy())
                    .updateTime(ThematicDataPo.getUpdateTime())
                    .build();
            return thematicListVo;
        }).collect(Collectors.toList());

        PageResult<ThematicListVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    @Override
    public Integer delThematicData(Integer id) {
        ThematicDataPo thematicDataPo = new ThematicDataPo();
        thematicDataPo.setId(id);
        thematicDataPo.del("admin");
        int num = baseMapper.updateById(thematicDataPo);
        if (num <= 0) {
            throw new JeecgBootException("sql delete error");
        }
        return id;
    }

    @Override
    public String editThematicData(ThematicEditDto thematicEditDto) {
        ThematicDataPo thematicDataPo = new ThematicDataPo();
        BeanUtils.copyProperties(thematicEditDto, thematicDataPo);
        thematicDataPo.initBase(false, "admin");
        int num = baseMapper.updateById(thematicDataPo);
        if (num <= 0) {
            throw new JeecgBootException("sql update error");
        }
        return thematicDataPo.getName();
    }

    private void checkParam(ThematicDataListDto thematicDataListDto) {
        thematicDataListDto.setPageNo(Optional.ofNullable(thematicDataListDto.getPageNo()).orElse(1));
        thematicDataListDto.setPageSize(Optional.ofNullable(thematicDataListDto.getPageSize()).orElse(10));
        thematicDataListDto.setOrderByType(Optional.ofNullable(thematicDataListDto.getOrderByType()).orElse("desc"));
        thematicDataListDto.setOrderByField(Optional.ofNullable(thematicDataListDto.getOrderByField()).orElse("update_time"));
    }
}




