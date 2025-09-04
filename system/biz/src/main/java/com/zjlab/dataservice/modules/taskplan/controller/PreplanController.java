package com.zjlab.dataservice.modules.taskplan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.myspace.enums.OrderStatusEnum;
import com.zjlab.dataservice.modules.taskplan.enums.CXStatusEnum;
import com.zjlab.dataservice.modules.taskplan.enums.CYKStatusEnum;
import com.zjlab.dataservice.modules.taskplan.model.dto.*;
import com.zjlab.dataservice.modules.taskplan.model.entity.TaskInfoEntity;
import com.zjlab.dataservice.modules.taskplan.model.po.PreplanDataPo;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.po.TcCxtaskPo;
import com.zjlab.dataservice.modules.taskplan.model.vo.*;
import com.zjlab.dataservice.modules.taskplan.service.MetadataGfService;
import com.zjlab.dataservice.modules.taskplan.service.PreplanDataService;
import com.zjlab.dataservice.modules.taskplan.service.TaskManageService;
import com.zjlab.dataservice.modules.taskplan.service.TcCxtaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/taskplan")
@Slf4j
public class PreplanController {
    @Resource
    private PreplanDataService preplanDataService;

    @Resource
    private TaskManageService taskManageService;

    @Resource
    private TcCxtaskService tcCxtaskService;

    @Resource
    private MetadataGfService metaDataService;

    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private MetadataGfService metadataGfService;

    @Value("${url.cyk}")
    private String url;

    @PostMapping("query")
    public Result<PreplanDataVo> qryThematicDataList(@Valid @RequestBody PreplanDataDto preplanDataDto) throws JsonProcessingException {
        PreplanDataVo query = preplanDataService.query(preplanDataDto);
        return Result.ok(query);
    }

    @PostMapping("date")
    public Result<QueryDateVo> qryDate() throws JsonProcessingException {
        QueryDateVo query = preplanDataService.queryDate();
        return Result.ok(query);
    }

    @PostMapping("add")
    public Result addTask(@RequestBody @Valid TcTaskAddDto tcTaskAddDto) {
        int tcTaskId = tcCxtaskService.addTcTask(tcTaskAddDto);
        // 如果一切顺利，返回成功的预定ID
        return Result.ok(tcTaskId);
    }

    @GetMapping("detail/{id}")
    public Result detail(@PathVariable Long id) {
        TcCxtaskPo tcCxtaskPo = tcCxtaskService.getDetail(id);
        // 如果一切顺利，返回成功的预定ID
        return Result.ok(tcCxtaskPo);
    }


    @PostMapping("add_beifen")
    @Transactional(rollbackFor = Exception.class)
    public Result addMark(@RequestBody @Valid TaskAddDto taskAddDto) {
        int preplanId = taskManageService.addTask(taskAddDto);
        List<PreplanDataPo> preplanGeom = taskAddDto.getPreplanGeom();
        // 轨道划分
        List<List<PreplanDataPo>> tracks = new ArrayList<>();
        List<PreplanDataPo> currentTrack = new ArrayList<>();
        PreplanDataPo previousPoint = preplanGeom.get(0);

        for (int i = 1; i < preplanGeom.size(); i++) {
            PreplanDataPo currentPoint = preplanGeom.get(i);
            if (previousPoint != null) {
                Duration duration = Duration.between(previousPoint.getTime(), currentPoint.getTime());
                if (duration.getSeconds() != 1) {
                    // 对当前轨迹进行插值
                    tracks.add(currentTrack); // 添加当前轨迹到轨迹列表中
                    currentTrack = new ArrayList<>(); // 创建一个新的轨迹
                }
            }
            currentTrack.add(currentPoint);
            previousPoint = currentPoint;
        }
        // 循环结束后，确保将最后的轨迹添加到轨迹列表中
        if (!currentTrack.isEmpty()) {
            tracks.add(currentTrack);
        }

        MessageVo messageVo = new MessageVo();
        List<TargetInfoVo> result = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            List<PreplanDataPo> group = tracks.get(i);
            String satelliteId = group.get(0).getSatelliteId();
            TargetInfoVo data = new TargetInfoVo();
            LocalDateTime startTime;
            LocalDateTime endTime;
            if (group.size() > 1) {
                startTime = group.get(0).getTime();
                endTime = group.get(group.size() - 1).getTime();
            } else if (group.size() == 1) {
                // 如果 group 只有一个元素时，startTime 和 endTime 都设置为该元素的时间
                startTime = group.get(0).getTime();
                endTime = startTime;
            } else {
                // 如为空时的处理逻辑（视情况而定）
                startTime = null;
                endTime = null;
            }
//            // 计算时间差
            Duration duration = Duration.between(startTime, endTime);
//            // 获取秒数
            double seconds = duration.getSeconds() + 1;
//            // 转换为时间戳
            long startTimestamp = getTimestampOfDateTime(startTime.minusNanos((long) (0.5 * 1_000_000_000)));
            long endTimestamp = getTimestampOfDateTime(endTime.plusNanos((long) (0.5 * 1_000_000_000)));
            // 计算经纬度的平均值
            double totalLatitude = 0.0;
            double totalLongitude = 0.0;
            int count = 0;
            for (PreplanDataPo obj : group) {
                double latitude = obj.getLatitude();
                double longitude = obj.getLongitude();
                totalLatitude += latitude;
                totalLongitude += longitude;
                count++;
            }
            totalLatitude /= count;
            totalLongitude /= count;
            data.setSatelliteId(satelliteId);
            data.setSatelliteName("一号卫星");
            data.setCamStartTime(startTimestamp);
            data.setCamEndTime(endTimestamp);
            data.setCamCenterLa(String.valueOf(totalLatitude));
            data.setCamCenterLo(String.valueOf(totalLongitude));
            data.setCamPeriod(seconds);
            result.add(data);
        }
        messageVo.setTargetInfoList(result);
        messageVo.setPlanId(preplanId);
        messageVo.setCreationTime(System.currentTimeMillis());
        messageVo.setPlanType("COMMON");
        messageVo.setOriginator("a");
        messageVo.setRecipient("b");
        messageVo.setMessageType("TASK");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MessageVo> entity = new HttpEntity<>(messageVo, headers);

            ResponseVo response = restTemplate.postForObject(url, entity, ResponseVo.class);

            if (response == null || response.getCode() != 200) {
                throw new RuntimeException("测运控接口请求失败，返回结果无效或不成功");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("远程接口请求错误: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("系统发生异常: " + e.getMessage());
        }

        // 如果一切顺利，返回成功的预定ID
        return Result.ok(preplanId);
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }


    @PostMapping("list")
    public Result<PageResult<TaskManageVo>> list(@RequestBody(required = false) TaskListDto taskListDto) {
        if (taskListDto == null) {
            taskListDto = new TaskListDto();
        }
        PageResult<TaskManageVo> result = taskManageService.qryTaskList(taskListDto);
        return Result.ok(result);
    }

    @PostMapping("/statistics")
    public Result<TaskStaticsVo> statistics() {
        TaskStaticsVo staticsVo = taskManageService.statistics();
        return Result.OK(staticsVo);
    }


    @PostMapping("/taskinfo")
    public Result<PageResult<MetadataVo>> getTaskInfo(@Valid @RequestBody TaskInfoDto taskInfoDto) {
        TaskManagePo info = taskManageService.getById(taskInfoDto.getId());
        TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
        taskInfoEntity.setEndTime(info.getEndTime());
        taskInfoEntity.setStartTime(info.getStartTime());
        taskInfoEntity.setGeom(info.getGeom());
        taskInfoEntity.setStatus(info.getStatus());
        taskInfoEntity.setPageNo(taskInfoDto.getPageNo());
        taskInfoEntity.setPageSize(taskInfoDto.getPageSize());
//        查询meta_info
        PageResult<MetadataVo> query = metaDataService.query(taskInfoEntity);
        return Result.OK(query);

    }

    @PostMapping("/taskinf")
    public Result<List<taskVo>> taskInfo() {
        List<taskVo> result = taskManageService.listByStatus(1);
        return Result.OK(result);
    }

    @PostMapping("/result")
    public Result updateManage(@Valid @RequestBody ResultDto resultDto) {
        TaskManagePo taskInfo = taskManageService.getById(resultDto.getPlanId());
        taskInfo.setPlanStatus(resultDto.getPlanStatus());
//        1.成功 2.部分成功 3.失败
//        if (resultDto.getPlanStatus() == CYKStatusEnum.SUCCESS.getStatus() || resultDto.getPlanStatus() == CYKStatusEnum.PATIAL_SUCCESS.getStatus()) {
//            String geo = taskInfo.getGeom();
//            LocalDateTime createTime = taskInfo.getStartTime();
//            MetadataVo metadataVoResult = metadataGfService.queryFile(geo, createTime);
//            if (metadataVoResult != null && metadataVoResult.getThumbFileLocation() != null && !metadataVoResult.getThumbFileLocation().isEmpty()) {
//                taskInfo.setThumbFile(metadataVoResult.getThumbFileLocation());
//            }
//            taskInfo.setStatus(CXStatusEnum.CONFIRE.getStatus());
//        } else {
//            taskInfo.setStatus(CXStatusEnum.FAIL.getStatus());
//        }
        if (resultDto.getPlanStatus() == CYKStatusEnum.FAIL.getStatus()) {
            taskInfo.setStatus(CXStatusEnum.FAIL.getStatus());
        }

        taskInfo.setOriginator(resultDto.getOriginator());
        taskInfo.setRecipient(resultDto.getRecipient());
        taskInfo.setPlanType(resultDto.getPlanType());
        if (resultDto.getTargetInfoList() != null) {
            taskInfo.setTargetInfoList(resultDto.getTargetInfoList().toString());
        }
//        taskInfo.setTargetInfoList(resultDto.getTargetInfoList().toString());
        taskInfo.setMessageType(resultDto.getMessageType());
        taskInfo.setCreationTime(resultDto.getCreationTime());
        boolean b = taskManageService.updateById(taskInfo);
        return Result.OK(b);
    }

}
