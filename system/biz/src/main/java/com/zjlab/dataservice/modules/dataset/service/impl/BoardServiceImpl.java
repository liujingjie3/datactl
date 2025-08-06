package com.zjlab.dataservice.modules.dataset.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.util.TimeUtils;
import com.zjlab.dataservice.modules.dataset.mapper.DatasetInfoMapper;
import com.zjlab.dataservice.modules.dataset.mapper.MarkFileMapper;
import com.zjlab.dataservice.modules.dataset.mapper.MetadataGfMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.MarkStatisticsRankDto;
import com.zjlab.dataservice.modules.dataset.model.entity.*;
import com.zjlab.dataservice.modules.dataset.model.vo.board.*;
import com.zjlab.dataservice.modules.dataset.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardServiceImpl implements BoardService {

    private static final Integer UNTREATED = 0;    //未处理
    private static final Integer UNFILTERED = 1;    //未筛选
    private static final Integer UNALLOCATED = 2;    //未分配
    private static final Integer MARKED = 3;    //已初标
    private static final Integer UNCHECKED = 4;    //待检查
    private static final Integer CHECKED = 5;    //已复查
    private static final String MONTHLY = "monthly";
    private static final String WEEKLY = "weekly";
    private static final String DAILY = "daily";

    @Resource
    private MarkFileMapper markFileMapper;
    @Resource
    private DatasetInfoMapper datasetInfoMapper;
    @Resource
    private MetadataGfMapper metadataGfMapper;

    @Override
    public BoardStatisticsVo statistics() {
        BoardStatisticsVo boardStatisticsVo = new BoardStatisticsVo();
        //三类统计，一类出错不影响其他数据返回
        try {
            MarkStatisticsVo markStatistics = getMarkStatistics();
            boardStatisticsVo.setMarkStatisticsVo(markStatistics);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        try {
            DatasetStatisticsVo datasetStatistics = getDatasetStatistics();
            boardStatisticsVo.setDatasetStatisticsVo(datasetStatistics);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        try {
            ImageStatisticsVo imageStatisticsVo = getImageStatisticsVo();
            boardStatisticsVo.setImageStatisticsVo(imageStatisticsVo);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return boardStatisticsVo;
    }

    private MarkStatisticsVo getMarkStatistics(){
        MarkStatisticsVo statisticsVo = new MarkStatisticsVo();
        Long total = markFileMapper.selectCount(null);
        statisticsVo.setTotal(Integer.parseInt(total.toString()));      //total
        List<StatusEntity> statusEntities = markFileMapper.qryStatusCount();

        //获取状态对应数量
        Integer untreated = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), UNTREATED)).map(StatusEntity::getCount).findFirst().orElse(0);
        Integer unfiltered = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), UNFILTERED)).map(StatusEntity::getCount).findFirst().orElse(0);
        Integer unallocated = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), UNALLOCATED)).map(StatusEntity::getCount).findFirst().orElse(0);
        Integer marked = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), MARKED)).map(StatusEntity::getCount).findFirst().orElse(0);
        Integer unchecked = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), UNCHECKED)).map(StatusEntity::getCount).findFirst().orElse(0);
        Integer checked = statusEntities.stream().filter(statusEntity ->
                Objects.equals(statusEntity.getStatus(), CHECKED)).map(StatusEntity::getCount).findFirst().orElse(0);
        statisticsVo.setUnMarkNum(unallocated);         //status=2
        statisticsVo.setAssignNum(unchecked);           //status=4
        statisticsVo.setMarkedNum(marked);              //status=3
        statisticsVo.setCheckedNum(checked);            //status=5

        //数据核查率 5/(4+5)
        double checkRatio;
        int checkRatioDenominator = unchecked + checked;
        if (checkRatioDenominator == 0){
            checkRatio = 0.00;
        }else {
            String checkRatioFormatted = String.format("%.2f", (double) checked / checkRatioDenominator);
            checkRatio = Double.parseDouble(checkRatioFormatted);
        }
        statisticsVo.setCheckRatio(checkRatio);
        // (status != 0 && status != 1) / total
        double filterRatio;
        if (total == 0){
            filterRatio = 0.00;
        }else {
            String filterRatioFormatted = String.format("%.2f", (double)(total - untreated - unfiltered) / total);
            filterRatio = Double.parseDouble(filterRatioFormatted);
        }
        statisticsVo.setFilterRatio(filterRatio);
        return statisticsVo;
    }

    private DatasetStatisticsVo getDatasetStatistics(){
        DatasetStatisticsVo datasetStatisticsVo = new DatasetStatisticsVo();
        //获取总数、入库(发布)数、入库率
        DatasetStatisticsEntity datasetStatisticsEntity = datasetInfoMapper.qryDatasetStatistics();
        Integer total = datasetStatisticsEntity.getTotal();
        Integer publicNum = datasetStatisticsEntity.getPublicNum();
        datasetStatisticsVo.setTotal(total);
        datasetStatisticsVo.setPublicNum(publicNum);
        double publicRatio;
        if (total == 0){
            publicRatio = 0.00;
        }else {
            String checkRatioFormatted = String.format("%.2f", (double) publicNum / total);
            publicRatio = Double.parseDouble(checkRatioFormatted);
        }
        datasetStatisticsVo.setPublicRatio(publicRatio);

        //月度统计list
        List<MonthlyPeriodEntity> monthlyList = datasetInfoMapper.qryMonthlyList();
        List<PeriodStatisticsVo> formattedPeriodList = monthlyFormatList(monthlyList);
        datasetStatisticsVo.setPeriodStatistics(formattedPeriodList);
        return datasetStatisticsVo;
    }

    private ImageStatisticsVo getImageStatisticsVo(){
        ImageStatisticsVo imageStatisticsVo = new ImageStatisticsVo();
        //获取总数
        Long count = metadataGfMapper.selectCount(null);
        imageStatisticsVo.setTotal(Integer.parseInt(count.toString()));
        //获取按月统计list
        List<MonthlyPeriodEntity> monthlyList = metadataGfMapper.qryMonthlyList();
        List<PeriodStatisticsVo> formattedPeriodList = monthlyFormatList(monthlyList);
        imageStatisticsVo.setMonthlyStatistics(formattedPeriodList);
        return imageStatisticsVo;
    }

    private List<PeriodStatisticsVo> monthlyFormatList(List<MonthlyPeriodEntity> monthlyList){
        List<PeriodStatisticsVo> collect = monthlyList.stream().map(monthlyEntity -> {
            String month = monthlyEntity.getMonth();    //example: 2024-05
            String[] split = month.split("-");
            Integer year = Integer.parseInt(split[0]);
            Integer id = Integer.parseInt(split[1]);
            Integer count = monthlyEntity.getCount();
            String firstDay = monthlyEntity.getFirstDay();
            PeriodStatisticsVo periodStatisticsVo = new PeriodStatisticsVo();
            periodStatisticsVo.setYear(year);
            periodStatisticsVo.setMode(MONTHLY);
            periodStatisticsVo.setId(id);
            periodStatisticsVo.setNum(count);
            periodStatisticsVo.setFirstDay(firstDay);
            return periodStatisticsVo;
        }).collect(Collectors.toList());
        return collect;
    }
    @Override
    public List<MarkStatisticsRankVo> statisticsRank(MarkStatisticsRankDto rankDto) {
        String startTime = null;
        String endTime = null;
        String dateFormat = "yyyy-MM-dd";
        if (rankDto != null){
            startTime = rankDto.getStartTime();
            if (startTime != null && !TimeUtils.isDateFormatValid(startTime, dateFormat)){
                throw new BaseException(ResultCode.DATEFORMAT_ERROR);
            }
            endTime = rankDto.getEndTime();
            if (endTime != null && !TimeUtils.isDateFormatValid(endTime, dateFormat)){
                throw new BaseException(ResultCode.DATEFORMAT_ERROR);
            }
        }
        List<MarkStatisticsRankEntity> rankEntities = markFileMapper.qryStatisticsRank(startTime, endTime);
        List<MarkStatisticsRankVo> rankVoList = rankEntities.stream().map(entity -> {
            MarkStatisticsRankVo rankVo = new MarkStatisticsRankVo();
            BeanUtils.copyProperties(entity, rankVo);
            return rankVo;
        }).collect(Collectors.toList());
        return rankVoList;
    }

    @Override
    public List<PeriodStatisticsVo> statisticsPeriod() {
        List<WeeklyPeriodEntity> weeklyList = markFileMapper.qryWeeklyList();
        List<PeriodStatisticsVo> collect = weeklyList.stream().map(weeklyEntity -> {
            String week = weeklyEntity.getWeek();   //example: 202427
            Integer year = Integer.parseInt(week.substring(0, 4));
            Integer id = Integer.parseInt(week.substring(4));
            Integer count = weeklyEntity.getCount();
            String firstDay = weeklyEntity.getFirstDay();
            PeriodStatisticsVo periodStatisticsVo = new PeriodStatisticsVo();
            periodStatisticsVo.setYear(year);
            periodStatisticsVo.setMode(WEEKLY);
            periodStatisticsVo.setId(id);
            periodStatisticsVo.setNum(count);
            periodStatisticsVo.setFirstDay(firstDay);
            return periodStatisticsVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void exportDailyExcel(HttpServletResponse response) throws IOException {
        //导出成excel
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式如 "20230917143000"
        String currentTime = sdf.format(new Date());
        String fileName = "未分配文件列表-" + currentTime + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileNameURL);

        //获取数据
        //语义分割
        List<DailyEntity> segmentDaily = markFileMapper.qrySegmentDailyStatistics();
        List<List<Object>> segmentData = formatData(segmentDaily, "语义分割");
        List<Object> segmentObjects = segmentData.get(segmentData.size() - 1);
        Object segmentCnt = segmentObjects.get(segmentObjects.size() - 1);

        //图像分类
        List<DailyEntity> classifyDaily = markFileMapper.qryClassifyDailyStatistics();
        List<List<Object>> classifyData = formatData(classifyDaily, "图像分类");
        List<Object> classifyObjects = classifyData.get(classifyData.size() - 1);
        Object classifyCnt = classifyObjects.get(classifyObjects.size() - 1);

        //图文解译
        List<DailyEntity> textDaily = markFileMapper.qryTextDailyStatistics();
        List<List<Object>> textData = formatData(textDaily, "图文解译");
        List<Object> textObjects = textData.get(textData.size() - 1);
        Object textCnt = textObjects.get(textObjects.size() - 1);

        //复审情况
        JSONObject json = new JSONObject();
        json.put("segmentCnt", segmentCnt);
        json.put("classifyCnt", classifyCnt);
        json.put("textCnt", textCnt);
        List<List<Object>> reviewData = getReviewData(json);

        //写入数据
        List<List<Object>> writeData = new ArrayList<>(segmentData);
        writeData.add(Arrays.asList("", ""));
        writeData.add(Arrays.asList("", ""));
        writeData.addAll(classifyData);
        writeData.add(Arrays.asList("", ""));
        writeData.add(Arrays.asList("", ""));
        writeData.addAll(textData);
        writeData.add(Arrays.asList("", ""));
        writeData.add(Arrays.asList("", ""));
        writeData.addAll(reviewData);

        EasyExcel.write(response.getOutputStream())
                .sheet("天基")
                .doWrite(writeData);
    }

    private List<List<Object>> formatData(List<DailyEntity> dailyEntityList, String type){
        Set<String> uniqueDate = dailyEntityList.stream()
                .map(DailyEntity::getDate).collect(Collectors.toCollection(TreeSet::new));

        //返回整理格式后的数据
        List<List<Object>> formatData = new ArrayList<>();
        //表头
        List<Object> headers = new ArrayList<>();
        headers.add(type);
        headers.addAll(uniqueDate);
        headers.add("合计");
        formatData.add(headers);
        //数据转换为二维数组
        Map<String, Map<String, Integer>> dataMap = new HashMap<>();
        for (DailyEntity daily : dailyEntityList){
            dataMap.putIfAbsent(daily.getName(), new HashMap<>());
            dataMap.get(daily.getName()).put(daily.getDate(), daily.getCount());
        }
        Map<String, Integer> dateSum = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : dataMap.entrySet()){
            String name = entry.getKey();
            Map<String, Integer> counts = entry.getValue();
            List<Object> data = new ArrayList<>();
            data.add(name);
            int total = 0;
            for (String date : uniqueDate){
                Integer count = counts.get(date);
                if (count == null){
                    data.add("");
                }else {
                    total += count;
                    dateSum.put(date, dateSum.getOrDefault(date, 0) + count);
                    data.add(count);
                }
            }
            data.add(total);
            formatData.add(data);
        }
        //总计
        List<Object> total = new ArrayList<>();
        total.add("总计");
        int finalSum = 0;
        for (String date :uniqueDate){
            total.add(dateSum.get(date));
            finalSum += dateSum.get(date);
        }
        total.add(finalSum);
        formatData.add(total);
        return formatData;
    }

    private List<List<Object>> getReviewData(JSONObject json){
        Object segmentCnt = json.get("segmentCnt");
        Object classifyCnt = json.get("classifyCnt");
        Object textCnt = json.get("textCnt");
        MarkReviewEntity markReviewEntity = markFileMapper.qryMarkReviewStatistics();
        List<List<Object>> reviewData = new ArrayList<>();
        List<Object> header = new ArrayList<>();
        header.add("");
        header.add("语义分割");
        header.add("图像分类");
        header.add("图文解译");
        reviewData.add(header);
        List<Object> member = new ArrayList<>();
        member.add("组员累计标注情况");
        member.add(segmentCnt);
        member.add(classifyCnt);
        member.add(textCnt);
        reviewData.add(member);
        List<Object> leader = new ArrayList<>();
        leader.add("组长累计复审情况");
        leader.add(markReviewEntity.getSegmentReviewCnt());
        leader.add(markReviewEntity.getClassifyReviewCnt());
        leader.add(markReviewEntity.getTextReviewCnt());
        reviewData.add(leader);
        return reviewData;
    }
}
