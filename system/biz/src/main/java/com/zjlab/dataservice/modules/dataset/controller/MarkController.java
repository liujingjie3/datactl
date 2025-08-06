package com.zjlab.dataservice.modules.dataset.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.dataset.aspect.OperationEnum;
import com.zjlab.dataservice.modules.dataset.aspect.OperationLog;
import com.zjlab.dataservice.modules.dataset.enums.TaskTypeEnum;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.*;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MarkFileDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MetadataVo;
import com.zjlab.dataservice.modules.dataset.service.MarkService;
import com.zjlab.dataservice.modules.dataset.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mark")
@Slf4j
public class MarkController {

    @Resource
    private MarkService markService;

    @Resource
    private OperationLogService logService;

    @PostMapping("list")
    public Result<PageResult<MarkFileDetailVo>> qryMarkList(@RequestBody(required = false) @Valid MarkListDto markListDto){
        PageResult<MarkFileDetailVo> result = markService.qryMarkList(markListDto);
        return Result.ok(result);
    }

    @PostMapping("assign")
    @OperationLog(action = OperationEnum.ASSIGN)
    public Result assignMarkFile(@RequestBody @Valid MarkAssignDto markAssignDto){
        String taskType = markAssignDto.getTaskType();
        if (TaskTypeEnum.FILTER.getType().equals(taskType)) {
            MetadataAssignDto assignDto = new MetadataAssignDto();
            BeanUtils.copyProperties(markAssignDto, assignDto);
            Integer num = markService.assignMetadata(assignDto);
            return Result.ok(num);
        }
        Integer num = markService.assignMarkFile(markAssignDto);
        return Result.ok(num);
    }

    @PostMapping("filter")
    @OperationLog(action = OperationEnum.FILTER)
    public Result filter(@RequestBody @Valid MarkFilterDto filterDto){
        markService.filter(filterDto);
        return Result.ok();
    }

    @PostMapping("add")
    @OperationLog(action = OperationEnum.ADD)
    public Result addMark(@RequestBody @Valid MarkAddDto markAddDto){
        markService.addMark(markAddDto);
        return Result.ok();
    }

    @PostMapping("addBatch")
    @OperationLog(action = OperationEnum.ADD)
    public Result addMark(@RequestBody @Valid MarkAddBatchDto markAddBatchDto){
        markService.addMarkBatch(markAddBatchDto);
        return Result.ok();
    }

    @PostMapping("edit")
    @OperationLog(action = OperationEnum.EDIT)
    public Result editMark(@RequestBody @Valid MarkEditDto markEditDto){
        markService.editMark(markEditDto);
        return Result.ok();
    }

//    @PostMapping("reset")
//    @OperationLog(action = OperationEnum.RESET)
//    public Result resetMark(@RequestBody @Valid MarkResetDto markResetDto){
//        markService.resetMark(markResetDto);
//        return Result.ok();
//    }

    @PostMapping("abandon")
    @OperationLog(action = OperationEnum.CHECK)
    public Result abandonMark(@RequestBody @Valid MarkAbandonDto abandonDto) throws Exception {
        String taskType = abandonDto.getTaskType();
        if (TaskTypeEnum.FILTER.getType().equals(taskType)) {
            MetadataAbandonDto metadataAbandonDto = new MetadataAbandonDto();
            BeanUtils.copyProperties(abandonDto, metadataAbandonDto);
            markService.abandonMetadata(metadataAbandonDto);
            return Result.ok();
        }
        markService.abandonMark(abandonDto);
        return Result.ok();
    }
    @PostMapping("check")
    @OperationLog(action = OperationEnum.CHECK)
    public Result checkMark(@RequestBody @Valid MarkCheckDto checkDto) throws Exception {
        markService.checkMark(checkDto);
        return Result.ok();
    }

    @PostMapping("checkClass")
    @OperationLog(action = OperationEnum.CLASSIFY)
    public Result checkMarkClass(@RequestBody @Valid MarkCheckClassDto checkClassDto) throws Exception {
        markService.checkMarkClass(checkClassDto);
        return Result.ok();
    }

    @PostMapping("review")
    @OperationLog(action = OperationEnum.REVIEW)
    public Result review(@RequestBody @Valid MarkReviewDto reviewDto) {
        String taskType = reviewDto.getTaskType();
        if (TaskTypeEnum.FILTER.getType().equals(taskType)) {
            MetadataReviewDto metadataReviewDto = new MetadataReviewDto();
            BeanUtils.copyProperties(reviewDto, metadataReviewDto);
            markService.reviewMetadata(metadataReviewDto);
            return Result.ok();
        }
        markService.review(reviewDto);
        return Result.ok();
    }

    @PostMapping("reviewBatch")
    @OperationLog(action = OperationEnum.REVIEW)
    public Result reviewBatch(@RequestBody @Valid MarkReviewBatchDto reviewBatchDto) {
        String taskType = reviewBatchDto.getTaskType();
        if (TaskTypeEnum.FILTER.getType().equals(taskType)) {
            MetadataReviewBatchDto metadataReviewBatchDto = new MetadataReviewBatchDto();
            BeanUtils.copyProperties(reviewBatchDto, metadataReviewBatchDto);
            markService.reviewBatchMetadata(metadataReviewBatchDto);
            return Result.ok();
        }
        markService.reviewBatch(reviewBatchDto);
        return Result.ok();
    }

    @PostMapping("operationLog")
    public Result<List<String>> operationLog(@RequestBody @Valid OperationLogDto operationLogDto){
        List<String> operationLogList = logService.operationLog(operationLogDto);
        return Result.ok(operationLogList);
    }

    @PostMapping("export")
    public void exportExcel(HttpServletResponse response, @RequestBody ExportExcelDto exportExcelDto) throws IOException {
        markService.exportExcel(response, exportExcelDto);
    }

    @PostMapping("metadata/list")
    public Result<PageResult<MetadataVo>> qryMetadataList(@RequestBody(required = false) @Valid MetadataListDto metadataListDto) throws IOException {
        PageResult<MetadataVo> result = markService.qryMetadataList(metadataListDto);
        return Result.ok(result);
    }

    @PostMapping("metadata/filter")
    @OperationLog(action = OperationEnum.FILTER_METADATA)
    public Result filterMetadata(@RequestBody @Valid MetadataFilterDto metadataFilterDto) throws Exception {
        markService.filterMetadata(metadataFilterDto);
        return Result.ok();
    }

    @PostMapping("segment")
    @OperationLog(action = OperationEnum.SEGMENT)
    public Result segmentMark(@RequestBody @Valid MarkSegmentDto markSegmentDto) throws Exception {
        markService.segmentMark(markSegmentDto);
        return Result.ok();
    }
}
