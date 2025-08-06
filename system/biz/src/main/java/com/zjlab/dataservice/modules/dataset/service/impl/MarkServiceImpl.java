package com.zjlab.dataservice.modules.dataset.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.common.util.storage.MinioUtil;
import com.zjlab.dataservice.modules.dataset.enums.TaskTypeEnum;
import com.zjlab.dataservice.modules.dataset.mapper.MarkFileMapper;
import com.zjlab.dataservice.modules.dataset.mapper.MetadataGfMapper;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.*;
import com.zjlab.dataservice.modules.dataset.model.entity.*;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetMarkFilePo;
import com.zjlab.dataservice.modules.dataset.model.po.MetadataGfPo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MarkExcelDataVo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MarkFileDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MetadataVo;
import com.zjlab.dataservice.modules.dataset.service.MarkService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.FastArrayList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MarkServiceImpl extends ServiceImpl<MarkFileMapper, DatasetMarkFilePo> implements MarkService {

    private static final String DEFAULT_MARK_TYPE = "";
    private static final Integer UNTREATED = 0;    //未处理
    private static final Integer UNFILTERED = 1;    //未筛选
    private static final Integer UNALLOCATED = 2;    //未分配
    private static final Integer MARKED = 3;    //已初标
    private static final Integer UNCHECKED = 4;    //待检查 -> 待标注
    private static final Integer CHECKED = 5;    //已复查 -> 已标注
    private static final Integer DISCARD = 6;    //已废弃
    private static final String BUCKET_NAME = "dspp";


    @Resource
    private ISysUserService userService;
    @Resource
    private MetadataGfMapper metadataGfMapper;

    @Override
    public PageResult<MarkFileDetailVo> qryMarkList(MarkListDto markListDto) {
        PageResult<MarkFileDetailVo> result = new PageResult<>();
        MarkListEntity entity = checkParam(markListDto);
        entity.setUserId(UserThreadLocal.getUserId());
        IPage<DatasetMarkFilePo> page = new Page<>(entity.getPageNo(), entity.getPageSize());
        IPage<DatasetMarkFilePo> markFileList = baseMapper.qryMarkFileList(page, entity);
        result.setPageNo((int) markFileList.getCurrent());
        result.setPageSize((int) markFileList.getSize());
        result.setTotal((int) markFileList.getTotal());

        List<DatasetMarkFilePo> records = markFileList.getRecords();
        if (CollectionUtils.isEmpty(records)){
            return result;
        }
        String taskType = entity.getTaskType();
        List<MarkFileDetailVo> collect = records.stream().map(datasetMarkFilePo -> {
            MarkFileDetailVo fileDetailVo = new MarkFileDetailVo();
            BeanUtils.copyProperties(datasetMarkFilePo, fileDetailVo);
            TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);
            SysUser markerUser;
            SysUser checkerUser;
            if (null != taskTypeEnum) {
                switch (taskTypeEnum) {
                    case CLASSIFY:
                        //获取标注人和核查人姓名
                        markerUser = userService.getById(datasetMarkFilePo.getClassifyMarker());
                        if (markerUser != null){
                            fileDetailVo.setMarkerName(markerUser.getRealname());
                        }
                        checkerUser = userService.getById(datasetMarkFilePo.getClassifyChecker());
                        if (checkerUser != null){
                            fileDetailVo.setCheckerName(checkerUser.getRealname());
                        }
                        fileDetailVo.setUpdateTime(datasetMarkFilePo.getClassifyUpdateTime());
                        fileDetailVo.setStatus(datasetMarkFilePo.getClassifyStatus());
                        fileDetailVo.setReview(datasetMarkFilePo.getClassifyReview());
                        fileDetailVo.setPurpose(datasetMarkFilePo.getClassifyPurpose());

                        break;
                    case SEGMENT:
                        //获取标注人和核查人姓名
                        markerUser = userService.getById(datasetMarkFilePo.getSegmentMarker());
                        if (markerUser != null){
                            fileDetailVo.setMarkerName(markerUser.getRealname());
                        }
                        checkerUser = userService.getById(datasetMarkFilePo.getSegmentChecker());
                        if (checkerUser != null){
                            fileDetailVo.setCheckerName(checkerUser.getRealname());
                        }
                        fileDetailVo.setUpdateTime(datasetMarkFilePo.getSegmentUpdateTime());
                        fileDetailVo.setStatus(datasetMarkFilePo.getSegmentStatus());
                        fileDetailVo.setReview(datasetMarkFilePo.getSegmentReview());
                        fileDetailVo.setPurpose(datasetMarkFilePo.getSegmentPurpose());

                        break;
                    case TEXT:
                    default:
                        //获取标注人和核查人姓名
                        markerUser = userService.getById(datasetMarkFilePo.getMarker());
                        if (markerUser != null){
                            fileDetailVo.setMarkerName(markerUser.getRealname());
                        }
                        checkerUser = userService.getById(datasetMarkFilePo.getChecker());
                        if (checkerUser != null){
                            fileDetailVo.setCheckerName(checkerUser.getRealname());
                        }
                        fileDetailVo.setReview(datasetMarkFilePo.getReview());

                        break;
                }
            }
            return fileDetailVo;
        }).collect(Collectors.toList());
        result.setData(collect);

        return result;
    }

    private MarkListEntity checkParam(MarkListDto markListDto) {
        if (markListDto == null){
            markListDto = new MarkListDto();
        }
        markListDto.setPageNo(Optional.ofNullable(markListDto.getPageNo()).orElse(1));
        markListDto.setPageSize(Optional.ofNullable(markListDto.getPageSize()).orElse(10));
        String field = markListDto.getOrderByField();
        if (StringUtils.isBlank(field)){
            markListDto.setOrderByField("update_time");
        }else {
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")){
                result = result.substring(1);
            }
            markListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(markListDto.getOrderByType())){
            markListDto.setOrderByType("desc");
        }
        MarkListEntity entity = new MarkListEntity();
        BeanUtils.copyProperties(markListDto, entity);
        return entity;
    }

    private MetadataListEntity checkParamMetadataList(MetadataListDto metadataListDto) {
        if (metadataListDto == null){
            metadataListDto = new MetadataListDto();
        }
        metadataListDto.setPageNo(Optional.ofNullable(metadataListDto.getPageNo()).orElse(1));
        metadataListDto.setPageSize(Optional.ofNullable(metadataListDto.getPageSize()).orElse(10));
        String field = metadataListDto.getOrderByField();
        if (StringUtils.isBlank(field)){
            metadataListDto.setOrderByField("update_time");
        }else {
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")){
                result = result.substring(1);
            }
            metadataListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(metadataListDto.getOrderByType())){
            metadataListDto.setOrderByType("desc");
        }
        MetadataListEntity entity = new MetadataListEntity();
        BeanUtils.copyProperties(metadataListDto, entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer assignMarkFile(MarkAssignDto markAssignDto) {
        String marker = markAssignDto.getMarker();
        List<Integer> ids = markAssignDto.getIds();
        Integer status = markAssignDto.getStatus();
        if (!Objects.equals(status, UNTREATED) && !status.equals(MARKED) && !status.equals(UNALLOCATED)){
            throw new BaseException(ResultCode.STATUS_INVALID);
        }

        String taskType = markAssignDto.getTaskType();
        int existIds = baseMapper.selectExistIds(ids, status, taskType);
        if (existIds != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_ID_NOT_EXIST);
        }
        AssignMarkFileEntity entity = new AssignMarkFileEntity();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);
        if (null != taskTypeEnum) {
            switch (taskTypeEnum) {
                case CLASSIFY:
                    entity.setIds(ids).setClassifyMarker(marker)
                            .setClassifyChecker(UserThreadLocal.getUserId())
                            .setClassifyUpdateBy(UserThreadLocal.getUserId())
                            .setClassifyUpdateTime(LocalDateTime.now());
                    if (status.equals(UNTREATED)) {
                        //0 -> 1
                        entity.setClassifyStatus(UNFILTERED);
                    } else {
                        //3 -> 4 或 2 -> 4
                        entity.setClassifyStatus(UNCHECKED);
                    }
                    break;
                case SEGMENT:
                    entity.setIds(ids).setSegmentMarker(marker)
                            .setSegmentChecker(UserThreadLocal.getUserId())
                            .setSegmentUpdateBy(UserThreadLocal.getUserId())
                            .setSegmentUpdateTime(LocalDateTime.now());
                    if (status.equals(UNTREATED)) {
                        //0 -> 1
                        entity.setSegmentStatus(UNFILTERED);
                    } else {
                        //3 -> 4 或 2 -> 4
                        entity.setSegmentStatus(UNCHECKED);
                    }
                    break;
                case TEXT:
                default:
                    entity.setIds(ids).setMarker(marker)
                            .setChecker(UserThreadLocal.getUserId())
                            .setCreateBy(UserThreadLocal.getUserId())
                            .setCreateTime(LocalDateTime.now())
                            .setUpdateBy(UserThreadLocal.getUserId())
                            .setUpdateTime(LocalDateTime.now());
                    if (status.equals(UNTREATED)) {
                        //0 -> 1
                        entity.setStatus(UNFILTERED);
                    } else {
                        //3 -> 4 或 2 -> 4
                        entity.setStatus(UNCHECKED);
                    }
                    break;
            }
        }
        int num = baseMapper.assignMarkFile(entity);
        if (num != ids.size()){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
        return num;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void filter(MarkFilterDto filterDto) {
        List<Integer> ids = filterDto.getIds();
        String userId = UserThreadLocal.getUserId();
        int existIds = baseMapper.selectExistIds(ids, UNFILTERED, "text");
        if (existIds != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_ID_NOT_EXIST_OR_INVALID);
        }
        MarkFilterEntity filterEntity = new MarkFilterEntity();
        filterEntity.setIds(ids)
                //1 -> 2
                //此时mapper将3种切片的任务都改变为 未分配
                .setStatus(UNALLOCATED)
                .setUpdateBy(userId)
                .setUpdateTime(LocalDateTime.now());
        int num = baseMapper.filter(filterEntity);
        if (num != ids.size()){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMark(MarkAddDto markAddDto) {
        Integer id = markAddDto.getId();
        String markType = markAddDto.getMarkType();
        String userId = UserThreadLocal.getUserId();

        checkFileExistAndStatus(id, UNALLOCATED, TaskTypeEnum.TEXT);
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        markFilePo.setMarkType(markType);
        //2 -> 3
        markFilePo.setStatus(MARKED);
        markFilePo.setUpdateBy(userId);
        markFilePo.setUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMarkBatch(MarkAddBatchDto markAddBatchDto) {
        List<Integer> ids = markAddBatchDto.getIds();
        Integer markType = markAddBatchDto.getMarkType();
        String userId = UserThreadLocal.getUserId();

        checkFileExistAndStatus(ids, UNALLOCATED);
        AddMarkBatchEntity addMarkBatchEntity = new AddMarkBatchEntity();
        addMarkBatchEntity.setIds(ids)
                .setMarkType(markType)
                //2 -> 3
                .setStatus(MARKED)
                .setUpdateBy(userId)
                .setUpdateTime(LocalDateTime.now());
        int num = baseMapper.addMarkBatch(addMarkBatchEntity);
        if (num != ids.size()){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    public void checkFileExistAndStatus(Integer id, Integer status, TaskTypeEnum taskType){
        QueryWrapper<DatasetMarkFilePo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(DatasetMarkFilePo::getId, id).eq(DatasetMarkFilePo::getDelFlag, false);
        DatasetMarkFilePo datasetMarkFilePo = baseMapper.selectOne(wrapper);
        if (datasetMarkFilePo == null){
            throw new BaseException(ResultCode.FILE_NOT_EXIST);
        }
        switch (taskType){
            case CLASSIFY:
                if (!Objects.equals(datasetMarkFilePo.getClassifyStatus(), status)){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
            case SEGMENT:
                if (!Objects.equals(datasetMarkFilePo.getSegmentStatus(), status)){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
            case TEXT:
            default:
                if (!Objects.equals(datasetMarkFilePo.getStatus(), status)){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
        }
    }

    public void checkFileExistAndStatuses(Integer id, List<Integer> statuses, TaskTypeEnum taskType){
        QueryWrapper<DatasetMarkFilePo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(DatasetMarkFilePo::getId, id).eq(DatasetMarkFilePo::getDelFlag, false);
        DatasetMarkFilePo datasetMarkFilePo = baseMapper.selectOne(wrapper);
        if (datasetMarkFilePo == null){
            throw new BaseException(ResultCode.FILE_NOT_EXIST);
        }
        switch (taskType){
            case CLASSIFY:
                //存在其中一种状态即可
                if (!statuses.contains(datasetMarkFilePo.getClassifyStatus())){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
            case SEGMENT:
                //存在其中一种状态即可
                if (!statuses.contains(datasetMarkFilePo.getSegmentStatus())){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
            case TEXT:
            default:
                //存在其中一种状态即可
                if (!statuses.contains(datasetMarkFilePo.getStatus())){
                    throw new BaseException(ResultCode.MARK_FILE_STATUS_NOT_MATCH);
                }
                break;
        }
    }

    public void checkFileExistAndStatus(List<Integer> ids, Integer status){
        QueryWrapper<DatasetMarkFilePo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(DatasetMarkFilePo::getId, ids).eq(DatasetMarkFilePo::getDelFlag, false);
        Long idsCount = baseMapper.selectCount(wrapper);
        log.info("check exist count : {}", idsCount);
        if (idsCount != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_FILE_NOT_EXIST);
        }
        wrapper.lambda().eq(DatasetMarkFilePo::getStatus, status);
        Long statusCount = baseMapper.selectCount(wrapper);
        log.info("check status count : {}", statusCount);
        if (statusCount != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_FILE_STATUS_NOT_MATCH);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editMark(MarkEditDto markEditDto) {
        Integer id = markEditDto.getId();
        String markType = markEditDto.getMarkType();
        String userId = UserThreadLocal.getUserId();

//        checkFileExistAndStatus(id, MARKED);
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        markFilePo.setMarkType(markType);
//        markFilePo.setStatus(MARKED);
        markFilePo.setUpdateBy(userId);
        markFilePo.setUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetMark(MarkResetDto markResetDto) {
        Integer id = markResetDto.getId();
        String userId = UserThreadLocal.getUserId();

//        checkFileExistAndStatus(id, MARKED);
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        markFilePo.setMarkType(DEFAULT_MARK_TYPE);
//        markFilePo.setStatus(ASSIGNED_UNMARK);
        markFilePo.setUpdateBy(userId);
        markFilePo.setUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void abandonMark(MarkAbandonDto abandonDto) {
        Integer id = abandonDto.getId();
        String userId = UserThreadLocal.getUserId();
        String taskType = abandonDto.getTaskType();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);
        checkFileExistAndStatus(id, UNCHECKED, taskTypeEnum);

        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        if (null != taskTypeEnum) {
            switch (taskTypeEnum) {
                case CLASSIFY:
                    //4 -> 6
                    markFilePo.setClassifyStatus(DISCARD);
                    markFilePo.setClassifyUpdateBy(userId);
                    markFilePo.setClassifyUpdateTime(LocalDateTime.now());
                    break;
                case SEGMENT:
                    //4 -> 6
                    markFilePo.setSegmentStatus(DISCARD);
                    markFilePo.setSegmentUpdateBy(userId);
                    markFilePo.setSegmentUpdateTime(LocalDateTime.now());
                    break;
                case TEXT:
                default:
                    //4 -> 6
                    markFilePo.setStatus(DISCARD);
                    markFilePo.setUpdateBy(userId);
                    markFilePo.setUpdateTime(LocalDateTime.now());
                    break;
            }
        }
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void abandonMetadata(MetadataAbandonDto abandonDto) {
        Integer id = abandonDto.getId();
        String userId = UserThreadLocal.getUserId();

        MetadataGfPo metadataGfPo = new MetadataGfPo();
        metadataGfPo.setId(id);
        //4 -> 6
        metadataGfPo.setStatus(DISCARD);
        metadataGfPo.setUpdateBy(userId);
//        metadataGfPo.setUpdateTime(LocalDateTime.now());
        metadataGfPo.setUpdateTime(new Date());

        int num = metadataGfMapper.updateById(metadataGfPo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkMark(MarkCheckDto checkDto) throws Exception {
        Integer id = checkDto.getId();
        String markUrl = checkDto.getMarkUrl();
        String xmlData = checkDto.getXmlData();
        Integer purpose = checkDto.getPurpose();
        String userId = UserThreadLocal.getUserId();

        //修改状态, 支持未校验改校验、重复校验
        List<Integer> statuses = new ArrayList<>();
        statuses.add(UNCHECKED);
        statuses.add(CHECKED);
        checkFileExistAndStatuses(id, statuses, TaskTypeEnum.TEXT);
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        //4 -> 5
        markFilePo.setStatus(CHECKED);
        markFilePo.setPurpose(purpose);
        markFilePo.setMarkUrl(markUrl);
        markFilePo.setUpdateBy(userId);
        markFilePo.setUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }

        //minio上传文件
        log.info("will upload xml to minio. path : {}", markUrl);
        InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
        MinioUtil.uploadFile(BUCKET_NAME, markUrl, inputStream);
        log.info("upload xml to minio success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkMarkClass(MarkCheckClassDto checkClassDto) throws Exception {
        Integer id = checkClassDto.getId();
        String markType = checkClassDto.getMarkType();
        Integer purpose = checkClassDto.getPurpose();
        String userId = UserThreadLocal.getUserId();

        //修改状态, 支持未校验改校验、重复校验
        List<Integer> statuses = new ArrayList<>();
        statuses.add(UNCHECKED);
        statuses.add(CHECKED);
        checkFileExistAndStatuses(id, statuses, TaskTypeEnum.CLASSIFY);
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(id);
        //4 -> 5
        markFilePo.setClassifyStatus(CHECKED);
        markFilePo.setClassifyPurpose(purpose);
        markFilePo.setMarkType(markType);
        markFilePo.setClassifyUpdateBy(userId);
        markFilePo.setClassifyUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(MarkReviewDto reviewDto) {
        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        markFilePo.setId(reviewDto.getId());

        String taskType = reviewDto.getTaskType();
        Integer review = reviewDto.getReview();
        if (!review.equals(1) && !review.equals(2)) {
            throw new BaseException(ResultCode.REVIEW_STATUS_INVALID);
        }
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);
        if (null != taskTypeEnum) {
            switch (taskTypeEnum) {
                case CLASSIFY:
                    markFilePo.setClassifyReview(review);
                    // 审核不通过 退回重标
                    if (review.equals(2)) {
                        markFilePo.setClassifyStatus(UNCHECKED);
                    }
                    break;
                case SEGMENT:
                    markFilePo.setSegmentReview(review);
                    if (review.equals(2)) {
                        markFilePo.setSegmentStatus(UNCHECKED);
                    }
                    break;
                case TEXT:
                default:
                    markFilePo.setReview(review);
                    if (review.equals(2)) {
                        markFilePo.setStatus(UNCHECKED);
                    }
                    break;
            }
        }
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void reviewMetadata(MetadataReviewDto reviewDto) {
        MetadataGfPo metadataGfPo = new MetadataGfPo();
        metadataGfPo.setId(reviewDto.getId());
        Integer review = reviewDto.getReview();
        if (!review.equals(1) && !review.equals(2)) {
            throw new BaseException(ResultCode.REVIEW_STATUS_INVALID);
        }
        metadataGfPo.setReview(review);
        if (review.equals(2)) {
            metadataGfPo.setStatus(UNCHECKED);
        }
        int num = metadataGfMapper.updateById(metadataGfPo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewBatch(MarkReviewBatchDto reviewBatchDto) {
        List<Integer> ids = reviewBatchDto.getIds();
        String taskType = reviewBatchDto.getTaskType();
        Integer review = reviewBatchDto.getReview();
        if (!review.equals(1) && !review.equals(2)) {
            throw new BaseException(ResultCode.REVIEW_STATUS_INVALID);
        }
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);
        for (int id : ids){
            DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
            markFilePo.setId(id);
            if (null != taskTypeEnum) {
                switch (taskTypeEnum) {
                    case CLASSIFY:
                        markFilePo.setClassifyReview(review);
                        // 审核不通过 退回重标
                        if (review.equals(2)) {
                            markFilePo.setClassifyStatus(UNCHECKED);
                        }
                        break;
                    case SEGMENT:
                        markFilePo.setSegmentReview(review);
                        if (review.equals(2)) {
                            markFilePo.setSegmentStatus(UNCHECKED);
                        }
                        break;
                    case TEXT:
                    default:
                        markFilePo.setReview(review);
                        if (review.equals(2)) {
                            markFilePo.setStatus(UNCHECKED);
                        }
                        break;
                }
            }
            int num = baseMapper.updateById(markFilePo);
            if (num <= 0){
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void reviewBatchMetadata(MetadataReviewBatchDto reviewBatchDto) {
        List<Integer> ids = reviewBatchDto.getIds();
        Integer review = reviewBatchDto.getReview();
        if (!review.equals(1) && !review.equals(2)) {
            throw new BaseException(ResultCode.REVIEW_STATUS_INVALID);
        }
        for (int id : ids){
            DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
            markFilePo.setId(id);
            markFilePo.setReview(review);
            if (review.equals(2)) {
                markFilePo.setStatus(UNCHECKED);
            }
            int num = baseMapper.updateById(markFilePo);
            if (num <= 0){
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
    }

    @Override
    public void exportExcel(HttpServletResponse response, ExportExcelDto exportExcelDto) throws IOException {
        //获取人员姓名
        String userId = UserThreadLocal.getUserId();
        SysUser markerUser = userService.getById(userId);
        String userName;
        if (markerUser != null){
            userName = markerUser.getRealname();
        } else {
            userName = "";
        }

        String taskType = exportExcelDto.getTaskType();
        Integer status = exportExcelDto.getStatus();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);

        //获取匹配到的数据
        QueryWrapper<DatasetMarkFilePo> wrapper = new QueryWrapper<>();
        if (null != taskTypeEnum) {
            switch (taskTypeEnum) {
                case FILTER:
                    exportExcelMetadata(response, exportExcelDto, userName);
                    return;
                case CLASSIFY:
                    wrapper.lambda().eq(DatasetMarkFilePo::getDelFlag, false)
                            .eq(DatasetMarkFilePo::getClassifyMarker, userId)
                            .eq(DatasetMarkFilePo::getClassifyStatus, status);
                    break;
                case SEGMENT:
                    wrapper.lambda().eq(DatasetMarkFilePo::getDelFlag, false)
                            .eq(DatasetMarkFilePo::getSegmentMarker, userId)
                            .eq(DatasetMarkFilePo::getSegmentStatus, status);
                    break;
                case TEXT:
                default:
                    wrapper.lambda().eq(DatasetMarkFilePo::getDelFlag, false)
                            .eq(DatasetMarkFilePo::getMarker, userId)
                            .eq(DatasetMarkFilePo::getStatus, status);
                    break;
            }
        }
        List<DatasetMarkFilePo> markFilePoList = baseMapper.selectList(wrapper);

        //导出成excel
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式如 "20230917143000"
        String currentTime = sdf.format(new Date());
        String fileName = "未分配文件列表-" + currentTime + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileNameURL);

        List<MarkExcelDataVo> collect = markFilePoList.stream().map(datasetMarkFilePo -> {
            MarkExcelDataVo excelDataVo = new MarkExcelDataVo();
            excelDataVo.setFileName(datasetMarkFilePo.getName());
            excelDataVo.setFileUrl(datasetMarkFilePo.getOriUrl());
            excelDataVo.setUserName(userName);
            return excelDataVo;
        }).collect(Collectors.toList());
        EasyExcel.write(response.getOutputStream(), MarkExcelDataVo.class)
                .sheet("Sheet1")
                .doWrite(collect); //表头依据@ExcelProperty注解自动生成
    }

    @DS("postgre")
    private void exportExcelMetadata(HttpServletResponse response, ExportExcelDto exportExcelDto, String userName) throws IOException {
        //获取人员姓名
        String userId = UserThreadLocal.getUserId();

        String taskType = exportExcelDto.getTaskType();
        Integer status = exportExcelDto.getStatus();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getEnumByType(taskType);

        //获取匹配到的数据
        QueryWrapper<MetadataGfPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MetadataGfPo::getMarker, userId)
                .eq(MetadataGfPo::getStatus, status);
        List<MetadataGfPo> metadataGfPoList = metadataGfMapper.selectList(wrapper);

        //导出成excel
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式如 "20230917143000"
        String currentTime = sdf.format(new Date());
        String fileName = "未分配文件列表-" + currentTime + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileNameURL);

        List<MarkExcelDataVo> collect = metadataGfPoList.stream().map(metadataGfPo -> {
            MarkExcelDataVo excelDataVo = new MarkExcelDataVo();
            excelDataVo.setFileName(metadataGfPo.getFileName());
            excelDataVo.setFileUrl(metadataGfPo.getFilePath());
            excelDataVo.setUserName(userName);
            return excelDataVo;
        }).collect(Collectors.toList());
        EasyExcel.write(response.getOutputStream(), MarkExcelDataVo.class)
                .sheet("Sheet1")
                .doWrite(collect); //表头依据@ExcelProperty注解自动生成
    }

    @Override
    public PageResult<MetadataVo> qryMetadataList(MetadataListDto metadataListDto) {
        PageResult<MetadataVo> result = new PageResult<>();
        MetadataListEntity entity = checkParamMetadataList(metadataListDto);
        entity.setUserId(UserThreadLocal.getUserId());
        if (Objects.equals(entity.getOrderByField(), "name")) {
            entity.setOrderByField("file_name");
        }
        IPage<MetadataGfPo> page = new Page<>(entity.getPageNo(), entity.getPageSize());
        IPage<MetadataGfPo> metadataList = metadataGfMapper.qryMetadataList(page, entity);
        result.setPageNo((int) metadataList.getCurrent());
        result.setPageSize((int) metadataList.getSize());
        result.setTotal((int) metadataList.getTotal());

        List<MetadataGfPo> records = metadataList.getRecords();
        if (CollectionUtils.isEmpty(records)){
            return result;
        }
        List<MetadataVo> collect = records.stream().map(metadataGfPo -> {
            MetadataVo metadataVo = new MetadataVo();
            BeanUtils.copyProperties(metadataGfPo, metadataVo);
            metadataVo.setBrowseFileUrl(metadataGfPo.getBrowseFileLocation());
            metadataVo.setThumbFileUrl(metadataGfPo.getThumbFileLocation());
            metadataVo.setName(metadataGfPo.getFileName());
            //获取标注人和核查人姓名
            SysUser markerUser = userService.getById(metadataGfPo.getMarker());
            if (markerUser != null){
                metadataVo.setMarkerName(markerUser.getRealname());
            }
            SysUser checkerUser = userService.getById(metadataGfPo.getChecker());
            if (checkerUser != null){
                metadataVo.setCheckerName(checkerUser.getRealname());
            }
            return metadataVo;
        }).collect(Collectors.toList());
        result.setData(collect);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void filterMetadata(MetadataFilterDto metadataFilterDto) {
        List<Integer> ids = metadataFilterDto.getIds();
        Boolean filterPassed = metadataFilterDto.getFilterPassed();

        String userId = UserThreadLocal.getUserId();

        MetadataFilterEntity filterEntity = new MetadataFilterEntity();
        filterEntity.setIds(ids)
                //4 -> 5
                .setStatus(CHECKED)
                .setFilterPassed(filterPassed)
                .setUpdateBy(userId)
                .setUpdateTime(LocalDateTime.now());
        int num = metadataGfMapper.filterMetadata(filterEntity);
        if (num != ids.size()){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public Integer assignMetadata(MetadataAssignDto metadataAssignDto) {
        String marker = metadataAssignDto.getMarker();
        List<Integer> ids = metadataAssignDto.getIds();
        Integer status = metadataAssignDto.getStatus();
        if (!Objects.equals(status, UNTREATED) && !status.equals(MARKED) && !status.equals(UNALLOCATED)){
            throw new BaseException(ResultCode.STATUS_INVALID);
        }
        int existIds = metadataGfMapper.selectExistIds(ids, status);
        if (existIds != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_ID_NOT_EXIST);
        }
        AssignMetadataEntity entity = new AssignMetadataEntity();
        entity.setIds(ids).setMarker(marker)
                .setChecker(UserThreadLocal.getUserId())
                .setCreateBy(UserThreadLocal.getUserId())
                .setCreateTime(LocalDateTime.now())
                .setUpdateBy(UserThreadLocal.getUserId())
                .setUpdateTime(LocalDateTime.now());
        if (status.equals(UNTREATED)){
            //0 -> 2
            entity.setStatus(UNALLOCATED);
        }else {
            //3 -> 4 或 2 -> 4
            entity.setStatus(UNCHECKED);
        }
        int num = metadataGfMapper.assignMetadata(entity);
        if (num != ids.size()){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
        return num;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void segmentMark(MarkSegmentDto markSegmentDto) throws Exception {
        Integer id = markSegmentDto.getId();
        String segmentJsonData = markSegmentDto.getSegmentJsonData();
        String segmentUrl = markSegmentDto.getSegmentUrl();
        Integer purpose = markSegmentDto.getPurpose();
        String userId = UserThreadLocal.getUserId();

        //修改状态, 支持未分割改校验、重复校验
        List<Integer> statuses = new ArrayList<>();
        statuses.add(UNCHECKED);
        statuses.add(CHECKED);
        checkFileExistAndStatuses(id, statuses, TaskTypeEnum.SEGMENT);

        DatasetMarkFilePo markFilePo = new DatasetMarkFilePo();
        //4 -> 5
        markFilePo.setSegmentStatus(CHECKED);
        markFilePo.setId(id);
        markFilePo.setSegmentUrl(segmentUrl);
        markFilePo.setSegmentPurpose(purpose);
        markFilePo.setSegmentUpdateBy(userId);
        markFilePo.setSegmentUpdateTime(LocalDateTime.now());
        int num = baseMapper.updateById(markFilePo);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }

        //minio上传文件
        log.info("will upload json to minio. path : {}", segmentUrl);
        InputStream inputStream = new ByteArrayInputStream(segmentJsonData.getBytes(StandardCharsets.UTF_8));
        MinioUtil.uploadFile(BUCKET_NAME, segmentUrl, inputStream);
        log.info("upload json to minio success");
    }
}
