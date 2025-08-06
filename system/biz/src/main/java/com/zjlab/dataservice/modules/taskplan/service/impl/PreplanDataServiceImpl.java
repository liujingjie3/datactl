package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjlab.dataservice.modules.taskplan.mapper.PreplanDataMapper;
import com.zjlab.dataservice.modules.taskplan.model.dto.PreplanDataDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.QueryCenterDto;
import com.zjlab.dataservice.modules.taskplan.model.dto.SubQueryCenterParam;
import com.zjlab.dataservice.modules.taskplan.model.entity.DomesticWeatherInfoDay;
import com.zjlab.dataservice.modules.taskplan.model.entity.ForeignWeatherInfoDay;
import com.zjlab.dataservice.modules.taskplan.model.entity.IWeatherInfoDay;
import com.zjlab.dataservice.modules.taskplan.model.entity.SatelliteImagingData;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import com.zjlab.dataservice.modules.taskplan.model.vo.PreplanDataVo;
import com.zjlab.dataservice.modules.taskplan.model.vo.QueryDateVo;
import com.zjlab.dataservice.modules.taskplan.model.vo.SatelliteStatus;
import com.zjlab.dataservice.modules.taskplan.service.PreplanDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【preplan_data】的数据库操作Service实现
 * @createDate 2024-07-22 16:59:10
 */
@Service
@Slf4j
public class PreplanDataServiceImpl extends ServiceImpl<PreplanDataMapper, PreplanDataPo>
        implements PreplanDataService {

    //    国内天气预报url
    private static final String DOMESTIC_API_URL = "https://api.open.geovisearth.com/v2/cn/city/professional";
    // 国际天气预报url
    private static final String FOREIGN_API_URL = "https://api.open.geovisearth.com/v2/global/day/professional";

    private static final String TOKEN = "914b8c662865ef6cf5be08ac38087c9a";
    @Autowired
    private RestTemplate restTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PreplanDataVo query(PreplanDataDto preplanDataDto) throws JsonProcessingException {
        checkParam(preplanDataDto);
        List<PreplanDataPo> query = baseMapper.query(preplanDataDto);
        PreplanDataVo vo = new PreplanDataVo();

        if (query == null || query.isEmpty()) {
            return vo;
        }

        // Set satellite ID list
        vo.setSatelliteIds(new ArrayList<>(extractSatelliteIds(query)));

        // 分轨处理
        List<List<PreplanDataPo>> tracks = groupIntoTracks(query);

        // 构建成像数据列表
        List<SatelliteImagingData> imagingDataList = buildImagingData(tracks);
        vo.setList(imagingDataList);

        // 天气预报信息、成像质量打分
        attachWeatherInfo(query);
        vo.setPreplanDataPos(query);
        long lowCount = query.stream()
                .filter(po -> po.getQuality() == 2)
                .count();
        vo.setLowCount((int) lowCount);
        return vo;
    }

    @Override
    public QueryDateVo queryDate() {
        QueryDateVo vo = baseMapper.queryDate();
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        List<String> existingIds = baseMapper.queryExistingSatelliteIds(now,vo.getEndTime());

        List<SatelliteStatus> predefinedSatellites = Arrays.asList(
                new SatelliteStatus("SCS-01-01", "SCS-01-01", false),
                new SatelliteStatus("SCS-01-02", "SCS-01-02", false),
                new SatelliteStatus("SCS-01-03", "SCS-01-03", false),
                new SatelliteStatus("SCS-01-04", "SCS-01-04", false),
                new SatelliteStatus("SCS-01-05", "SCS-01-05", false),
                new SatelliteStatus("SCS-01-06", "SCS-01-06", false),
                new SatelliteStatus("SCS-01-07", "SCS-01-07", false),
                new SatelliteStatus("SCS-01-08", "SCS-01-08", false),
                new SatelliteStatus("SCS-01-09", "SCS-01-09", false),
                new SatelliteStatus("SCS-01-10", "SCS-01-10", false),
                new SatelliteStatus("SCS-01-11", "SCS-01-11", false),
                new SatelliteStatus("SCS-01-12", "SCS-01-12", false)
        );
        for (SatelliteStatus satellite : predefinedSatellites) {
            if (existingIds.contains(satellite.getId())) {
                satellite.setExists(true);
            }
        }
        vo.setSatellites(predefinedSatellites);
        return vo;
    }

    private void checkParam(PreplanDataDto preplanDataDto) {
        preplanDataDto.setOrderByType(Optional.ofNullable(preplanDataDto.getOrderByType()).orElse("asc"));
        preplanDataDto.setOrderByField(Optional.ofNullable(preplanDataDto.getOrderByField()).orElse("time"));
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    private Set<String> extractSatelliteIds(List<PreplanDataPo> query) {
        return query.stream()
                .map(PreplanDataPo::getSatelliteId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static final long TRACK_TIME_INTERVAL_SECONDS = 2;

    private List<List<PreplanDataPo>> groupIntoTracks(List<PreplanDataPo> query) {
        List<List<PreplanDataPo>> tracks = new ArrayList<>();
        List<PreplanDataPo> currentTrack = new ArrayList<>();
        PreplanDataPo previousPoint = query.get(0);
        currentTrack.add(previousPoint);

        for (int i = 1; i < query.size(); i++) {
            PreplanDataPo currentPoint = query.get(i);
            Duration duration = Duration.between(previousPoint.getTime(), currentPoint.getTime());
            if (duration.getSeconds() != TRACK_TIME_INTERVAL_SECONDS) {
                tracks.add(currentTrack);
                currentTrack = new ArrayList<>();
            }
            currentTrack.add(currentPoint);
            previousPoint = currentPoint;
        }

        if (!currentTrack.isEmpty()) {
            tracks.add(currentTrack);
        }

        return tracks;
    }

    private List<SatelliteImagingData> buildImagingData(List<List<PreplanDataPo>> tracks) {
        List<SatelliteImagingData> result = new ArrayList<>();
        for (List<PreplanDataPo> group : tracks) {
            if (group == null || group.isEmpty()) continue;
            String satelliteIdStr = group.get(0).getSatelliteId();
            LocalDateTime startTime = group.get(0).getTime();
            LocalDateTime endTime = group.size() > 1 ? group.get(group.size() - 1).getTime() : startTime;
            Duration duration = Duration.between(startTime, endTime);
            // 获取秒数,每一个格子是两秒
            double camPeriod = duration.getSeconds() + 2;
            long startTimestamp = getTimestampOfDateTime(startTime.minusSeconds(1));
            long endTimestamp = getTimestampOfDateTime(endTime.plusSeconds(1));
            //平均侧摆角度
            Double offNadirDeg = group.stream()
                    .filter(p -> p.getOffNadirDeg() != null)
                    .mapToDouble(PreplanDataPo::getOffNadirDeg)
                    .average()
                    .orElse(0.0);

            Integer elevator = group.get(0).getElevator();
            //预计成像的数量
            int count = group.size();
            // 判断当前 group 是 center 还是侧摆
            boolean isCenter = "center".equalsIgnoreCase(group.get(0).getDirection());
            double swe, swn, nee, nen;
            if (isCenter) {
                if (group.size() == 1) {
                    swe = nee = group.get(0).getLongitude();
                    swn = nen = group.get(0).getLatitude();
                } else {
                    swe = group.get(0).getLongitude(); // 起点经度
                    swn = group.get(0).getLatitude();  // 起点纬度
                    nee = group.get(group.size() - 1).getLongitude(); // 终点经度
                    nen = group.get(group.size() - 1).getLatitude();  // 终点纬度
                }
            } else {
                if (group.size() == 1) {
                    SubQueryCenterParam param = new SubQueryCenterParam();
                    param.setSatelliteId(satelliteIdStr);
                    param.setTime(startTime);
                    QueryCenterDto queryDto = new QueryCenterDto();
                    queryDto.setConditions(Collections.singletonList(param));
                    List<PreplanDataPo> queryCenter = this.baseMapper.queryCenter(queryDto);
                    swe = nee = queryCenter.get(0).getLongitude();
                    swn = nen = queryCenter.get(0).getLatitude();
                } else {
                    SubQueryCenterParam param1 = new SubQueryCenterParam();
                    //起始点
                    param1.setSatelliteId(satelliteIdStr);
                    param1.setTime(startTime);
                    SubQueryCenterParam param2 = new SubQueryCenterParam();
                    //终止点
                    param2.setSatelliteId(satelliteIdStr);
                    param2.setTime(endTime);
                    List<SubQueryCenterParam> list = new ArrayList<>();
                    list.add(param1);
                    list.add(param2);
                    QueryCenterDto queryDto = new QueryCenterDto();
                    queryDto.setConditions(list);
                    List<PreplanDataPo> queryCenter = this.baseMapper.queryCenter(queryDto);
                    swe = queryCenter.get(0).getLongitude(); // 起点经度
                    swn = queryCenter.get(0).getLatitude();  // 起点纬度
                    nee = queryCenter.get(queryCenter.size() - 1).getLongitude(); // 终点经度
                    nen = queryCenter.get(queryCenter.size() - 1).getLatitude();  // 终点纬度
                }
            }
            // 拍摄中心坐标（经纬度字符串）
            String camCenterLo = String.valueOf((swe + nee) / 2.0);
            String camCenterLa = String.valueOf((swn + nen) / 2.0);
            SatelliteImagingData imagingData = new SatelliteImagingData();
            imagingData.setSatelliteId(satelliteIdStr);
            imagingData.setCount(count);
            imagingData.setStartTimestamp(startTimestamp);
            imagingData.setEndTimestamp(endTimestamp);
            imagingData.setSwe(swe);
            imagingData.setSwn(swn);
            imagingData.setNee(nee);
            imagingData.setNen(nen);
            imagingData.setCamPeriod(camPeriod);
            imagingData.setCamCenterLa(camCenterLa);
            imagingData.setCamCenterLo(camCenterLo);
            imagingData.setOffNadirDeg(offNadirDeg);
            imagingData.setElevator(elevator);
            result.add(imagingData);
        }

        return result;
    }

//    private void attachWeatherInfo(List<PreplanDataPo> query) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//
//        for (PreplanDataPo po : query) {
//            double lat = po.getLatitude();
//            double lon = po.getLongitude();
//            String url = buildWeatherUrl(lat, lon);
//            String targetDate = po.getTime().format(formatter);
//
//            try {
//                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//                JsonNode root = objectMapper.readTree(response.getBody());
//                JsonNode matched = findMatchingWeather(root, targetDate);
//                if (matched != null) {
//                        boolean domestic = isDomestic(lat, lon);  // 新增这一行
//                        IWeatherInfoDay day = domestic
//                                ? objectMapper.treeToValue(matched, DomesticWeatherInfoDay.class)
//                                : objectMapper.treeToValue(matched, ForeignWeatherInfoDay.class);
//                        po.setWeatherJson(day);
//                        po.setDomestic(domestic);  // 新增这一行
//                    }
//            } catch (Exception e) {
//                log.info("天气接口调用失败: {}", e.getMessage());
//            }
//
//            // 模拟图像质量 0: 高 1: 中 2: 差
//            po.setQuality(predictImageQuality(po));
//        }
//    }

    private static final ExecutorService weatherExecutor = Executors.newFixedThreadPool(10);
    private void attachWeatherInfo(List<PreplanDataPo> query) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<Future<?>> futures = new ArrayList<>();

        for (PreplanDataPo po : query) {
            Future<?> future = weatherExecutor.submit(() -> {
                double lat = po.getLatitude();
                double lon = po.getLongitude();
                String url = buildWeatherUrl(lat, lon);
                String targetDate = po.getTime().format(formatter);
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode matched = findMatchingWeather(root, targetDate);
                    if (matched != null) {
                        boolean domestic = isDomestic(lat, lon);  // 新增这一行
                        IWeatherInfoDay day = domestic
                                ? objectMapper.treeToValue(matched, DomesticWeatherInfoDay.class)
                                : objectMapper.treeToValue(matched, ForeignWeatherInfoDay.class);
                        po.setWeatherJson(day);
                        po.setDomestic(domestic);  // 新增这一行
                    }
                } catch (Exception e) {
                    log.info("天气接口调用失败: {}", e.getMessage());
                }

                // 图像质量预测（模拟）
                po.setQuality(predictImageQuality(po));
            });
            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();  // 可设置 timeout
            } catch (Exception e) {
                log.error("并发处理失败", e);
            }
        }
    }


    private String buildWeatherUrl(double lat, double lon) {
        String location = String.format(Locale.US, "%.6f,%.6f", lon, lat);
        String api = isDomestic(lat, lon) ? DOMESTIC_API_URL : FOREIGN_API_URL;
        return String.format("%s?location=%s&token=%s", api, location, TOKEN);
    }

    private boolean isDomestic(double lat, double lon) {
        return lat >= 18.0 && lat <= 54.0 && lon >= 73.0 && lon <= 136.0;
    }

    private JsonNode findMatchingWeather(JsonNode root, String targetFcDate) {
        if (root.path("status").asInt(1) != 0) return null;
        JsonNode datas = root.path("result").path("datas");
        if (datas.isArray()) {
            for (JsonNode node : datas) {
                if (targetFcDate.equals(node.path("fc_time").asText())) {
                    return node;
                }
            }
        }
        return null;
    }

    private int predictImageQuality(PreplanDataPo po) {
        boolean domestic = po.isDomestic();
        IWeatherInfoDay weatherJson = po.getWeatherJson();
        if (weatherJson == null) {
            return 3; // 没有天气信息，视为差
        }
        double visibility = 0; // km
        double precipitation = 999;
        boolean isClear = false;
        boolean lowCloud = false;
        int cloudCover = 100;

        if (domestic && weatherJson instanceof DomesticWeatherInfoDay) {
            DomesticWeatherInfoDay d = (DomesticWeatherInfoDay) weatherJson;
            visibility = d.getVis() / 1000.0; // 米转 km
            precipitation = d.getPre_day() + d.getPre_night();
            isClear = "00".equals(d.getWp_day_code()); // 晴天编码
        } else if (!domestic && weatherJson instanceof ForeignWeatherInfoDay) {
            ForeignWeatherInfoDay f = (ForeignWeatherInfoDay) weatherJson;
            precipitation = f.getPre();
            cloudCover = f.getCloud_cover();
            lowCloud = cloudCover < 20;
        }

        // 判断成像质量
        if ((domestic && visibility > 10 && precipitation < 1 && isClear)
                || (!domestic && precipitation < 1 && lowCloud)) {
            return 0; // 最佳
        } else if ((domestic && visibility > 5 && precipitation < 3)
                || (!domestic && precipitation < 3 && cloudCover < 50)) {
            return 1; // 一般
        } else {
            return 2; // 差
        }
    }


//        checkParam(preplanDataDto);
//        List<PreplanDataPo> query = this.baseMapper.query(preplanDataDto);
//        PreplanDataVo preplanDataVo = new PreplanDataVo();
//        preplanDataVo.setPreplanDataPos(query);
//        // 使用 Stream API 统计出现的 satelliteId
//        Set<String> satelliteIds = query.stream()
//                .map(PreplanDataPo::getSatelliteId)  // 获取 satelliteId
//                .filter(id -> id != null)              // 过滤掉 null 值
//                .collect(Collectors.toSet());
//        List<String> result = new ArrayList<>(satelliteIds);
//        preplanDataVo.setSatelliteIds(result);
//        // 大功能点1：分轨处理
//        List<List<PreplanDataPo>> tracks = new ArrayList<>();
//        List<PreplanDataPo> currentTrack = new ArrayList<>();
//        PreplanDataPo previousPoint = query.get(0);
//        currentTrack.add(previousPoint);
//        for (int i = 1; i < query.size(); i++) {
//            PreplanDataPo currentPoint = query.get(i);
//            if (previousPoint != null) {
//                Duration duration = Duration.between(previousPoint.getTime(), currentPoint.getTime());
//                if (duration.getSeconds() != 2) {
//                    // 对当前轨迹进行插值
//                    tracks.add(currentTrack); // 添加当前轨迹到轨迹列表中
//                    currentTrack = new ArrayList<>(); // 创建一个新的轨迹
//                }
//            }
//            currentTrack.add(currentPoint);
//            previousPoint = currentPoint;
//        }
//        // 循环结束后，确保将最后的轨迹添加到轨迹列表中
//        if (!currentTrack.isEmpty()) {
//            tracks.add(currentTrack);
//        }
//
//        // 大功能点2：拼传给测运控以及前端展示的数据
//        List<SatelliteImagingData> imagingDataList = new ArrayList<>();
//        //需要判断tracks里的轨迹是中心轨迹还是侧摆轨迹，侧摆轨迹需要找到
//        for (int i = 0; i < tracks.size(); i++) {
//            List<PreplanDataPo> group = tracks.get(i);
//            if (group == null || group.isEmpty()) continue;
//            String satelliteIdStr = group.get(0).getSatelliteId();
//            LocalDateTime startTime = group.get(0).getTime();
//            LocalDateTime endTime = group.size() > 1 ? group.get(group.size() - 1).getTime() : startTime;
//            Duration duration = Duration.between(startTime, endTime);
//            // 获取秒数,每一个格子是两秒
//            double camPeriod = duration.getSeconds() + 2;
//            long startTimestamp = getTimestampOfDateTime(startTime.minusSeconds(1));
//            long endTimestamp = getTimestampOfDateTime(endTime.plusSeconds(1));
//            //平均侧摆角度
//            Double offNadirDeg = group.stream()
//                    .filter(p -> p.getOffNadirDeg() != null)
//                    .mapToDouble(PreplanDataPo::getOffNadirDeg)
//                    .average()
//                    .orElse(0.0);
//
//            Integer elevator = group.get(0).getElevator();
//            //预计成像的数量
//            int count = group.size();
//            // 判断当前 group 是 center 还是侧摆
//            boolean isCenter = "center".equalsIgnoreCase(group.get(0).getDirection());
//            double swe, swn, nee, nen;
//            if (isCenter) {
//                if (group.size() == 1) {
//                    swe = nee = group.get(0).getLongitude();
//                    swn = nen = group.get(0).getLatitude();
//                } else {
//                    swe = group.get(0).getLongitude(); // 起点经度
//                    swn = group.get(0).getLatitude();  // 起点纬度
//                    nee = group.get(group.size() - 1).getLongitude(); // 终点经度
//                    nen = group.get(group.size() - 1).getLatitude();  // 终点纬度
//                }
//            } else {
//                if (group.size() == 1) {
//                    SubQueryCenterParam param = new SubQueryCenterParam();
//                    param.setSatelliteId(satelliteIdStr);
//                    param.setTime(startTime);
//                    QueryCenterDto queryDto = new QueryCenterDto();
//                    queryDto.setConditions(Collections.singletonList(param));
//                    List<PreplanDataPo> queryCenter = this.baseMapper.queryCenter(queryDto);
//                    swe = nee = queryCenter.get(0).getLongitude();
//                    swn = nen = queryCenter.get(0).getLatitude();
//                } else {
//                    SubQueryCenterParam param1 = new SubQueryCenterParam();
//                    //起始点
//                    param1.setSatelliteId(satelliteIdStr);
//                    param1.setTime(startTime);
//                    SubQueryCenterParam param2 = new SubQueryCenterParam();
//                    //终止点
//                    param2.setSatelliteId(satelliteIdStr);
//                    param2.setTime(endTime);
//                    List<SubQueryCenterParam> list = new ArrayList<>();
//                    list.add(param1);
//                    list.add(param2);
//                    QueryCenterDto queryDto = new QueryCenterDto();
//                    queryDto.setConditions(list);
//                    List<PreplanDataPo> queryCenter = this.baseMapper.queryCenter(queryDto);
//                    swe = queryCenter.get(0).getLongitude(); // 起点经度
//                    swn = queryCenter.get(0).getLatitude();  // 起点纬度
//                    nee = queryCenter.get(queryCenter.size() - 1).getLongitude(); // 终点经度
//                    nen = queryCenter.get(queryCenter.size() - 1).getLatitude();  // 终点纬度
//                }
//            }
//            // 拍摄中心坐标（经纬度字符串）
//            String camCenterLo = String.valueOf((swe + nee) / 2.0);
//            String camCenterLa = String.valueOf((swn + nen) / 2.0);
//            SatelliteImagingData imagingData = new SatelliteImagingData();
//            imagingData.setSatelliteId(satelliteIdStr);
//            imagingData.setCount(count);
//            imagingData.setStartTimestamp(startTimestamp);
//            imagingData.setEndTimestamp(endTimestamp);
//            imagingData.setSwe(swe);
//            imagingData.setSwn(swn);
//            imagingData.setNee(nee);
//            imagingData.setNen(nen);
//            imagingData.setCamPeriod(camPeriod);
//            imagingData.setCamCenterLa(camCenterLa);
//            imagingData.setCamCenterLo(camCenterLo);
//            imagingData.setOffNadirDeg(offNadirDeg);
//            imagingData.setElevator(elevator);
//            imagingDataList.add(imagingData);
//        }
//        preplanDataVo.setList(imagingDataList);
//
//        //大功能点3：天气预报信息，\\待开发
//        int lowCount = 0;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        for (PreplanDataPo po : query) {
//            Double lat = po.getLatitude();
//            Double lon = po.getLongitude();
//            String locationParam = String.format(Locale.US, "%.6f,%.6f", lon, lat);
//            String targetFcDate = po.getTime().format(formatter);
//            String url;
//            boolean isDomestic = (lat >= 18.0 && lat <= 54.0 && lon >= 73.0 && lon <= 136.0); // 判断经纬度范围是否在国内
//            if (isDomestic) {
//                url = String.format("%s?location=%s&token=%s", DOMESTIC_API_URL, locationParam, TOKEN);
//            } else {
//                url = String.format("%s?location=%s&token=%s", FOREIGN_API_URL, locationParam, TOKEN);
//            }
//
//            JsonNode matched = null;
//
//            try {
//                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//                JsonNode root = objectMapper.readTree(response.getBody());
//                if (root.path("status").asInt(1) != 0) {
//                    continue; // status 非0，数据无效
//                }
//                JsonNode datas = root.path("result").path("datas");
//                if (datas.isArray()) {
//                    for (JsonNode node : datas) {
//                        String fcTime = node.path("fc_time").asText();
//                        if (targetFcDate.equals(fcTime)) {
//                            matched = node;
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                continue;
//            }
//
//            if (matched != null) {
//                IWeatherInfoDay day;
//                try {
//                    if (isDomestic) {
//                        day = objectMapper.treeToValue(matched, DomesticWeatherInfoDay.class);
//                    } else {
//                        day = objectMapper.treeToValue(matched, ForeignWeatherInfoDay.class);
//                    }
//                    po.setWeatherJson(day);  // 设置统一对象
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            //模型推理待开发
//            Random random = new Random();
//
//            po.setQuality(random.nextInt(3));
//
//        }
//        //待补充
//        preplanDataVo.setLowCount(lowCount);
//        preplanDataVo.setPreplanDataPos(query);
//        //预计有多少影响受天气影响
//        return preplanDataVo;

}




