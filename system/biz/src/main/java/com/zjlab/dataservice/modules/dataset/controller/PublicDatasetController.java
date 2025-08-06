package com.zjlab.dataservice.modules.dataset.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.*;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetFileVo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetListVo;
import com.zjlab.dataservice.modules.dataset.service.DatasetDetailService;
import com.zjlab.dataservice.modules.dataset.service.DatasetInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/dataset")
@Slf4j
public class PublicDatasetController {

    @Resource
    private DatasetInfoService datasetService;

    @Resource
    private DatasetDetailService detailService;

    @PostMapping("list")
    public Result<PageResult<DatasetListVo>> qryDatasetList(@RequestBody DatasetListDto datasetListDto){
        PageResult<DatasetListVo> datasetList = datasetService.qryDatasetList(datasetListDto);
        return Result.OK(datasetList);
    }

    @PostMapping("detail")
    public Result<DatasetDetailVo> qryDatasetDetail(@Valid @RequestBody DatasetDetailDto detailDto){
        DatasetDetailVo detailVo = datasetService.qryDatasetDetail(detailDto.getId());
        return Result.OK(detailVo);
    }

    @PostMapping("detail/file")
    public Result<PageResult<DatasetFileVo>> qryDatasetFile (@Valid @RequestBody DatasetFileListDto fileListDto){
        PageResult<DatasetFileVo> datasetFileList = detailService.qryDatasetFile(fileListDto);
        return Result.ok(datasetFileList);
    }

    @PostMapping("add")
    public Result addDataset(@Valid @RequestBody DatasetAddDto addDto){
        String add = datasetService.addDataset(addDto);
        return Result.OK("", add);
    }

    @PostMapping("edit")
    public Result editDataset(@Valid @RequestBody DatasetEditDto editDto){
        String edit = datasetService.editDataset(editDto);
        return Result.OK("", edit);
    }

    @PostMapping("del")
    public Result editDataset(@Valid @RequestBody DatasetDeleteDto deleteDto){
        Integer del = datasetService.delDataset(deleteDto.getId());
        return Result.OK(del);
    }
}
