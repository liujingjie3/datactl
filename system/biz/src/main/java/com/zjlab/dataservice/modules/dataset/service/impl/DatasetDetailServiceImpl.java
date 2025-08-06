package com.zjlab.dataservice.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.mapper.DatasetDetailMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetFileListDto;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetDetailPo;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetInfoPo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetFileVo;
import com.zjlab.dataservice.modules.dataset.service.DatasetDetailService;
import com.zjlab.dataservice.modules.dataset.service.DatasetInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatasetDetailServiceImpl extends ServiceImpl<DatasetDetailMapper, DatasetDetailPo> implements DatasetDetailService {

    @Resource
    private DatasetInfoService infoService;

    private static final String TABLE_NAME_PREFIX = "dataset_detail_id_";
    @Override
    public PageResult<DatasetFileVo> qryDatasetFile( DatasetFileListDto fileListDto) {
        checkParam(fileListDto);
        IPage<DatasetDetailPo> page = new Page<>(fileListDto.getPageNo(), fileListDto.getPageSize());
        DatasetInfoPo datasetInfoPo = infoService.getById(fileListDto.getId());
        String tableName = TABLE_NAME_PREFIX + datasetInfoPo.getId();
        IPage<DatasetDetailPo> fileList = baseMapper.qryFileList(page, tableName);
        List<DatasetDetailPo> fileListRecords = fileList.getRecords();
        if (CollectionUtils.isEmpty(fileListRecords)){
            return new PageResult<>();
        }
        List<DatasetFileVo> collect = fileListRecords.stream().map(datasetDetailPo -> {
            DatasetFileVo datasetFileVo = new DatasetFileVo();
            BeanUtils.copyProperties(datasetDetailPo, datasetFileVo);
            datasetFileVo.setDatasetId(datasetInfoPo.getId());
            datasetFileVo.setUpdateBy(Optional.ofNullable(datasetDetailPo.getUpdateBy()).orElse(datasetDetailPo.getCreateBy()));
            datasetFileVo.setUpdateTime(Optional.ofNullable(datasetDetailPo.getUpdateTime()).orElse(datasetDetailPo.getCreateTime()));
            return datasetFileVo;
        }).collect(Collectors.toList());

        PageResult<DatasetFileVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    private void checkParam(DatasetFileListDto fileListDto) {
        fileListDto.setPageNo(Optional.ofNullable(fileListDto.getPageNo()).orElse(1));
        fileListDto.setPageSize(Optional.ofNullable(fileListDto.getPageSize()).orElse(10));
        String field = fileListDto.getOrderByField();
        if (StringUtils.isBlank(field)){
            fileListDto.setOrderByField("update_time");
        }else {
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")){
                result = result.substring(1);
            }
            fileListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(fileListDto.getOrderByType())){
            fileListDto.setOrderByType("desc");
        }
    }
}
