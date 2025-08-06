package com.zjlab.dataservice.modules.dataset.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.exception.JeecgBootException;
import com.zjlab.dataservice.modules.dataset.mapper.DatasetInfoMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetAddDto;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetEditDto;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetListDto;
import com.zjlab.dataservice.modules.dataset.model.entity.DatasetInfoEntity;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetInfoPo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetListVo;
import com.zjlab.dataservice.modules.dataset.service.DatasetInfoService;
import com.zjlab.dataservice.modules.system.service.ISysDictItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetInfoServiceImpl extends ServiceImpl<DatasetInfoMapper, DatasetInfoPo>implements DatasetInfoService {

    @Resource
    private ISysDictItemService sysDictItemService;

    @Override
    public PageResult<DatasetListVo> qryDatasetList(DatasetListDto datasetListDto) {
        //接口入参 参数校验
        checkParam(datasetListDto);
        IPage<DatasetInfoPo> page = new Page<>(datasetListDto.getPageNo(), datasetListDto.getPageSize());
        //整理sql查询参数
        DatasetInfoEntity infoEntity = new DatasetInfoEntity();
        BeanUtils.copyProperties(datasetListDto, infoEntity);
        List<String> tags = convertStringToList(datasetListDto.getTags());
        infoEntity.setTags(tags);

        //查询结果
        IPage<DatasetInfoPo> datasetList = baseMapper.qryDatasetList(page, infoEntity);
        List<DatasetInfoPo> listRecords = datasetList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)){
            return new PageResult<>();
        }
        //查询tags数据暂存内存中
        List<JSONObject> totalTagsList = sysDictItemService.datasetTagsList();
        Map<String, JSONObject> map = new HashMap<>();
        for (JSONObject json : totalTagsList){
            map.put(json.getString("tagId"), json);
        }
        List<DatasetListVo> collect = listRecords.stream().map(datasetInfoPo -> {
            DatasetListVo datasetListVo = new DatasetListVo();
            BeanUtils.copyProperties(datasetInfoPo, datasetListVo);
            //整理tags数据信息,包含id,name,type; 以jsonObject格式返回
            List<JSONObject> datasetTagsList = new ArrayList<>();
            List<String> datasetTags = convertStringToList(datasetInfoPo.getTags());
            if (datasetTags != null){
                for (String tag : datasetTags){
                    datasetTagsList.add(map.get(tag));
                }
            }
            datasetListVo.setTagsObj(datasetTagsList);
            return datasetListVo;
        }).collect(Collectors.toList());

        //整理返回结果格式
        PageResult<DatasetListVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;

    }

    private void checkParam(DatasetListDto datasetListDto) {
        datasetListDto.setIsPublic(Optional.ofNullable(datasetListDto.getIsPublic()).orElse(true));
        datasetListDto.setPageNo(Optional.ofNullable(datasetListDto.getPageNo()).orElse(1));
        datasetListDto.setPageSize(Optional.ofNullable(datasetListDto.getPageSize()).orElse(10));
        String field = datasetListDto.getOrderByField();
        if (StringUtils.isBlank(field)){
            datasetListDto.setOrderByField("update_time");
        }else {
            //驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")){
                result = result.substring(1);
            }
            datasetListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(datasetListDto.getOrderByType())){
            datasetListDto.setOrderByType("desc");
        }
    }

    public static List<String> convertStringToList(String input) {
        if (input == null || input.isEmpty()) {
            return null;  // 返回一个空列表
        }
        // 使用逗号分割字符串并转换为列表
        return Arrays.asList(input.split(","));
    }

    @Override
    public DatasetDetailVo qryDatasetDetail(Integer id) {
        baseMapper.addViewCount(id);
        //查询tags数据暂存内存中 todo 是否要优化存储地址
        List<JSONObject> totalTagsList = sysDictItemService.datasetTagsList();
        Map<String, JSONObject> map = new HashMap<>();
        for (JSONObject json : totalTagsList){
            map.put(json.getString("tagId"), json);
        }
        DatasetInfoPo datasetInfoPo = baseMapper.selectById(id);
        DatasetDetailVo detailVo = new DatasetDetailVo();
        BeanUtils.copyProperties(datasetInfoPo, detailVo);
        //整理tags数据信息,包含id,name,type; 以jsonObject格式返回
        List<JSONObject> datasetTagsList = new ArrayList<>();
        List<String> datasetTags = convertStringToList(datasetInfoPo.getTags());
        if (datasetTags != null){
            for (String tag : datasetTags){
                datasetTagsList.add(map.get(tag));
            }
        }
        detailVo.setTagsObj(datasetTagsList);
        return detailVo;
    }

    @Override
    public DatasetInfoPo qryDetailByTitle(String title) {
        QueryWrapper<DatasetInfoPo> query = new QueryWrapper<DatasetInfoPo>();
        query.lambda().eq(DatasetInfoPo::getTitle, title)
                .eq(DatasetInfoPo::getDelFlag, false);
        return baseMapper.selectOne(query);
    }

    @Override
    public String addDataset(DatasetAddDto addDto) {
        DatasetInfoPo datasetInfoPo = new DatasetInfoPo();
        BeanUtils.copyProperties(addDto, datasetInfoPo);
        datasetInfoPo.initBase(true, "admin");
        int num = baseMapper.insert(datasetInfoPo);
        if (num <= 0){
            throw new JeecgBootException("sql insert error");
        }
        return datasetInfoPo.getTitle();
    }

    @Override
    public String editDataset(DatasetEditDto editDto) {
        DatasetInfoPo datasetInfoPo = new DatasetInfoPo();
        BeanUtils.copyProperties(editDto, datasetInfoPo);
        datasetInfoPo.initBase(false, "admin");
        int num = baseMapper.updateById(datasetInfoPo);
        if (num <= 0){
            throw new JeecgBootException("sql update error");
        }
        return datasetInfoPo.getTitle();
    }

    @Override
    public Integer delDataset(Integer id) {
        DatasetInfoPo datasetInfoPo = new DatasetInfoPo();
        datasetInfoPo.setId(id);
        datasetInfoPo.del("admin");
        int num = baseMapper.updateById(datasetInfoPo);
        if (num <= 0){
            throw new JeecgBootException("sql delete error");
        }
        return id;
    }


}
