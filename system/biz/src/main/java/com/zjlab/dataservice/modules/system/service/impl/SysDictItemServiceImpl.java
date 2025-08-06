package com.zjlab.dataservice.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjlab.dataservice.modules.system.entity.SysDictItem;
import com.zjlab.dataservice.modules.system.mapper.SysDictItemMapper;
import com.zjlab.dataservice.modules.system.model.ClassificationVo;
import com.zjlab.dataservice.modules.system.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public List<JSONObject> datasetTagsList() {
        String datasetTagId = "1790634812545478657";
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysDictItem::getDictId, datasetTagId)
                .eq(SysDictItem::getStatus, 1);
        wrapper.orderByAsc("sort_order");
        List<SysDictItem> list = baseMapper.selectList(wrapper);
        List<JSONObject> collect = list.stream().map(sysDictItem -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tagId", sysDictItem.getItemValue());
            jsonObject.put("tagName", sysDictItem.getItemText());
            jsonObject.put("tagType", sysDictItem.getDescription());
            return jsonObject;
        }).collect(Collectors.toList());
        List<String> order = Arrays.asList("数据来源", "数据功能", "应用场景", "更新频次");
        List<JSONObject> ordered = collect.stream().sorted(Comparator.comparingInt(
                tag -> order.indexOf(tag.getString("tagType")))
        ).collect(Collectors.toList());
        return ordered;
    }

    @Override
    public List<JSONObject> datasetClassList() {
        String datasetTagId = "1811693678981824514";
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysDictItem::getDictId, datasetTagId)
                .eq(SysDictItem::getStatus, 1);
        wrapper.orderByAsc("sort_order");
        List<SysDictItem> list = baseMapper.selectList(wrapper);
        List<JSONObject> collect = list.stream().map(sysDictItem -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tagId", sysDictItem.getItemValue());
            jsonObject.put("tagName", sysDictItem.getItemText());
            jsonObject.put("tagType", sysDictItem.getDescription());
            return jsonObject;
        }).collect(Collectors.toList());
        List<String> order = Arrays.asList("农业", "商业", "工业", "公共事业", "住宅", "交通", "未开发", "水域");
        List<JSONObject> ordered = collect.stream().sorted(Comparator.comparingInt(
                tag -> order.indexOf(tag.getString("tagType")))
        ).collect(Collectors.toList());
        return ordered;
    }

    @Override
    public List<JSONObject> segmentClassList() {
        String datasetTagId = "1813402863926882305";
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysDictItem::getDictId, datasetTagId)
                .eq(SysDictItem::getStatus, 1);
        wrapper.orderByAsc("sort_order");
        List<SysDictItem> list = baseMapper.selectList(wrapper);
        List<JSONObject> collect = list.stream().map(sysDictItem -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tagId", sysDictItem.getItemValue());
            jsonObject.put("tagName", sysDictItem.getItemText());
            String description = sysDictItem.getDescription();
            String[] parts = description.split("-");
            if (parts.length == 2) {
                jsonObject.put("tagType", parts[0]);
                jsonObject.put("tagColor", parts[1]);
            }
            return jsonObject;
        }).collect(Collectors.toList());
        List<String> order = Arrays.asList("建筑类", "交通类", "运动场地类", "自然资源类");
        List<JSONObject> ordered = collect.stream().sorted(Comparator.comparingInt(
                tag -> order.indexOf(tag.getString("tagType")))
        ).collect(Collectors.toList());
        return ordered;
    }

    @Override
    public List<ClassificationVo> selectList() {
        QueryWrapper<SysDictItem> query = new QueryWrapper<SysDictItem>();
        query.eq("dict_id", "1801491565500694530");
        query.select("item_text", "item_value", "description", "sort_order");
        query.orderByAsc("sort_order");
        List<SysDictItem> list = sysDictItemMapper.selectList(query);

        List<ClassificationVo> collec = list.stream().map(SysDictItem -> {
            ClassificationVo classificationVo = ClassificationVo.builder()
                    .classficationName(SysDictItem.getItemText())
                    .classficationValue(SysDictItem.getItemValue())
                    .description(SysDictItem.getDescription())
                    .sortOrder(SysDictItem.getSortOrder())
                    .build();
            return classificationVo;
        }).collect(Collectors.toList());
        return collec;
    }
}
