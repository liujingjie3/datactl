package com.zjlab.dataservice.modules.screen.service.impl;

import com.zjlab.dataservice.modules.myspace.enums.ImageTypeEnum;
import com.zjlab.dataservice.modules.screen.mapper.Gf3Mapper;
import com.zjlab.dataservice.modules.screen.mapper.GfMapper;
import com.zjlab.dataservice.modules.screen.model.entity.GF2TimeSeqEntity;
import com.zjlab.dataservice.modules.screen.model.entity.GF3TimeSeqEntity;
import com.zjlab.dataservice.modules.screen.model.entity.RegionDataEntity;
import com.zjlab.dataservice.modules.screen.model.vo.DataRegionStatisticsVo;
import com.zjlab.dataservice.modules.screen.model.vo.SliceStatisticsVo;
import com.zjlab.dataservice.modules.screen.model.vo.TimeSequenceVo;
import com.zjlab.dataservice.modules.screen.model.vo.YearSequenceVo;
import com.zjlab.dataservice.modules.screen.service.DataScreenService;
import com.zjlab.dataservice.modules.screen.service.RegionalStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataScreenServiceImpl implements DataScreenService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private GfMapper gfMapper;
    @Resource
    private Gf3Mapper gf3Mapper;

    @Resource
    private RegionalStatisticsService statisticsService;

    private static final Map<String, String> DATA_TYPE_MAP = new HashMap<>();
    static {
        DATA_TYPE_MAP.put("Hyperspectral ", "高光谱");
        DATA_TYPE_MAP.put("Multispectral", "多光谱");
        DATA_TYPE_MAP.put("Noctilucent", "夜光");
        DATA_TYPE_MAP.put("SAR", "SAR");
        DATA_TYPE_MAP.put("DEM", "DEM");
    }
    @Override
    public SliceStatisticsVo qrySliceStatistics() {
        long startTime = System.currentTimeMillis();
        SliceStatisticsVo statisticsVo = new SliceStatisticsVo();
        long samples = mongoTemplate.getCollection("samples").estimatedDocumentCount();
        statisticsVo.setTotal(samples);
        statisticsVo.setSlice(samples);
        long gf2count = gfMapper.count(null);
        long gf3count = gf3Mapper.count(null);
        statisticsVo.setL1(gf2count);
        statisticsVo.setInitial(gf2count + gf3count);
        long gf2L2 = gfMapper.selectL2();
        statisticsVo.setL2(gf2L2);
        long gf2L3 = gfMapper.selectL3();
         statisticsVo.setL3(gf2L3);
        long endTime = System.currentTimeMillis();
        log.info("screen slice cost time : {}", endTime - startTime);
        return statisticsVo;
    }

    @Override
    public List<TimeSequenceVo> qryDataTimeSequence(String regionCode) {
        Map<String, TimeSequenceVo> timeSeqMap = new HashMap<>();
        List<GF2TimeSeqEntity> gf2TimeSeqEntities = gfMapper.selectGF2TimeSeq(regionCode);
        List<GF3TimeSeqEntity> gf3TimeSeqEntities = gf3Mapper.selectGF3TimeSeq(regionCode);

        //合并gf2数据
        for (GF2TimeSeqEntity gf2Entity : gf2TimeSeqEntities){
            String year = gf2Entity.getYear();
            TimeSequenceVo timeSequenceVo = timeSeqMap.getOrDefault(year, new TimeSequenceVo());
            timeSequenceVo.setYear(year)
                    .setGF2(gf2Entity.getGf2())
                    .setRawData(timeSequenceVo.getRawData() + gf2Entity.getGf2());
            timeSeqMap.put(year, timeSequenceVo);
        }
        //合并gf3数据
        for (GF3TimeSeqEntity gf3Entity : gf3TimeSeqEntities){
            String year = gf3Entity.getYear();
            TimeSequenceVo timeSequenceVo = timeSeqMap.getOrDefault(year, new TimeSequenceVo());
            timeSequenceVo.setYear(year)
                    .setGF3(gf3Entity.getGf3())
                    .setRawData(timeSequenceVo.getRawData() + gf3Entity.getGf3());
            timeSeqMap.put(year, timeSequenceVo);
        }

        List<TimeSequenceVo> list = new ArrayList<>(timeSeqMap.values());
        list.sort(Comparator.comparingInt(vo -> Integer.parseInt(vo.getYear())));
        return list;
    }

    @Override
    public List<YearSequenceVo<DataRegionStatisticsVo>> qryDataRegionStatistics(String regionId) {
        long startTime = System.currentTimeMillis();
        List<YearSequenceVo<DataRegionStatisticsVo>> list = new ArrayList<>();
        //获取数据
        List<RegionDataEntity> gf2RegionData = gfMapper.selectGF2RegionData(regionId);
        List<RegionDataEntity> gf3RegionData = gf3Mapper.selectGF3RegionData(regionId);
//        //按市行政单位分组，计算时间覆盖率
//        List<RegionDataEntity> allRegionData = new ArrayList<>();
//        allRegionData.addAll(gf2RegionData);
//        allRegionData.addAll(gf3RegionData);
//        Map<String, String> timeCoverage = allRegionData.stream()
//                .filter(entity -> entity.getRegionCode() != null && entity.getYear() != null)
//                .collect(Collectors.groupingBy(
//                        RegionDataEntity::getRegionCode,
//                        Collectors.collectingAndThen(
//                                Collectors.mapping(RegionDataEntity::getYear, Collectors.toSet()),
//                                years ->{
//                                    String minYear = years.stream().min(Comparator.naturalOrder()).orElse(null);
//                                    String maxYear = years.stream().max(Comparator.naturalOrder()).orElse(null);
//                                    return minYear + "-" + maxYear;
//                                }
//                        )
//                ));
        //按时间和省份分类
        Map<String, Map<String, List<RegionDataEntity>>> collectGf2 = gf2RegionData.stream()
                .filter(entity -> entity.getRegionCode() != null)   //过滤掉regionCode为空的数据
                .collect(Collectors.groupingBy(
                RegionDataEntity::getYear, Collectors.groupingBy(RegionDataEntity::getRegionCode)));
        Map<String, Map<String, List<RegionDataEntity>>> collectGf3 = gf3RegionData.stream()
                .filter(entity -> entity.getRegionCode() != null)   //过滤掉regionCode为空的数据
                .collect(Collectors.groupingBy(
                RegionDataEntity::getYear, Collectors.groupingBy(RegionDataEntity::getRegionCode)));
        //合并按年份分组的数据
        Set<String> allYears = new HashSet<>(collectGf2.keySet());
        allYears.addAll(collectGf3.keySet());
        allYears.parallelStream().forEach(year -> {
            Map<String, List<RegionDataEntity>> gf2DataByYear = collectGf2.getOrDefault(year, new HashMap<>());
            Map<String, List<RegionDataEntity>> gf3DataByYear = collectGf3.getOrDefault(year, new HashMap<>());
            //整合省份数据
            Set<String> allRegionCodes = new HashSet<>(gf2DataByYear.keySet());
            allRegionCodes.addAll(gf3DataByYear.keySet());

            List<DataRegionStatisticsVo> dataRegionStatisticsVos = parallelDealRegionCode(allRegionCodes, year, gf2DataByYear, gf3DataByYear);
            dataRegionStatisticsVos.sort(Comparator.comparingLong(DataRegionStatisticsVo::getGF2).reversed());
            //创建year对象
            YearSequenceVo<DataRegionStatisticsVo> yearSequenceVo = new YearSequenceVo<>();
            yearSequenceVo.setYear(year);
            yearSequenceVo.setData(dataRegionStatisticsVos);
            synchronized (list){
                list.add(yearSequenceVo);
            }
        });
        //按年份排序
        list.sort(Comparator.comparingInt(vo -> Integer.parseInt(vo.getYear())));

        //增加累计节点，与年份节点同地位
        YearSequenceVo<DataRegionStatisticsVo> totalNode = new YearSequenceVo<>();
        List<DataRegionStatisticsVo> totalStatistics = new ArrayList<>();
        //累加数据
        list.forEach(item -> {
            List<DataRegionStatisticsVo> data = item.getData();
            data.forEach(regionData -> {
                Optional<DataRegionStatisticsVo> existingEntry = totalStatistics.stream()
                        .filter(result -> result.getRegionCode().equals(regionData.getRegionCode())).findFirst();
                if (existingEntry.isPresent()){
                    DataRegionStatisticsVo existingData = existingEntry.get();
                    existingData.setGF2(existingData.getGF2() + regionData.getGF2());
                    existingData.setGF3(existingData.getGF3() + regionData.getGF3());
                }else {
                    DataRegionStatisticsVo newEntry = new DataRegionStatisticsVo();
                    newEntry.setRegionCode(regionData.getRegionCode());
                    newEntry.setRegionName(regionData.getRegionName());
                    newEntry.setGF2(regionData.getGF2());
                    newEntry.setGF3(regionData.getGF3());
                    totalStatistics.add(newEntry);
                }
            });
        });
        //整理返回
        List<DataRegionStatisticsVo> totalStatisticsVo = totalStatistics.stream().map(total -> {
            Double coverageRate = statisticsService.getCoverageRate(total.getRegionCode(), "total",
                    getImageType(total.getGF2(), total.getGF3()));
            return new DataRegionStatisticsVo(total.getRegionCode(), total.getRegionName(), total.getGF2(),
                    total.getGF3(), coverageRate + "%");
        }).sorted(Comparator.comparingLong(DataRegionStatisticsVo::getGF2).reversed()).collect(Collectors.toList());
        totalNode.setYear("累计");
        totalNode.setData(totalStatisticsVo);
        list.add(totalNode);
        long endTime = System.currentTimeMillis();
        log.info("screen data region cost time : {}", endTime - startTime);
        return list;
    }

    private List<DataRegionStatisticsVo> parallelDealRegionCode(Set<String> allRegionCodes, String year,
                                                                Map<String, List<RegionDataEntity>> gf2DataByYear,
                                                                Map<String, List<RegionDataEntity>> gf3DataByYear){
        List<DataRegionStatisticsVo> dataRegionStatisticsVos = allRegionCodes.parallelStream().map(regionCode -> {
            List<RegionDataEntity> gf2RegionDataList = gf2DataByYear.getOrDefault(regionCode, Collections.emptyList());
            List<RegionDataEntity> gf3RegionDataList = gf3DataByYear.getOrDefault(regionCode, Collections.emptyList());

            long gf2Sum = gf2RegionDataList.stream().mapToLong(RegionDataEntity::getGf2).sum();  // gf2 数据汇总
            long gf3Sum = gf3RegionDataList.stream().mapToLong(RegionDataEntity::getGf3).sum();  // gf3 数据汇总

            String regionName = !gf2RegionDataList.isEmpty() ? gf2RegionDataList.get(0).getRegionName() :
                    (!gf3RegionDataList.isEmpty() ? gf3RegionDataList.get(0).getRegionName() : "Unknown");

            Double coverageRate = statisticsService.getCoverageRate(regionCode, year, getImageType(gf2Sum, gf3Sum));
            return new DataRegionStatisticsVo(regionCode, regionName, gf2Sum, gf3Sum, coverageRate + "%");
        }).collect(Collectors.toList());
        return dataRegionStatisticsVos;
    }

    private String getImageType(long gf2Sum, long gf3Sum) {
        if (gf2Sum > 0 && gf3Sum > 0){
            //不算混合的，按GF2的来
            return ImageTypeEnum.GF2.getType();
        } else if (gf2Sum > 0) {
            return ImageTypeEnum.GF2.getType();
        }else {
            return ImageTypeEnum.GF3.getType();
        }
    }

}
