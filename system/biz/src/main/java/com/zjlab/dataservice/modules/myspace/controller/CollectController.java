package com.zjlab.dataservice.modules.myspace.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.myspace.model.dto.collect.*;
import com.zjlab.dataservice.modules.myspace.model.vo.CollectImageDetailVo;
import com.zjlab.dataservice.modules.myspace.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/myspace/collect")
@Slf4j
public class CollectController {

    @Resource
    private CollectService collectService;

    @PostMapping("mkdir")
    public Result collectMkdir(@RequestBody @Valid CollectMkdirDto mkdirDto){
        collectService.collectMkdir(mkdirDto);
        return Result.OK();
    }

    @PostMapping("move")
    public Result collectMovePath(@RequestBody @Valid CollectMovePathDto movePathDto){
        collectService.collectMovePath(movePathDto);
        return Result.OK();
    }

    @PostMapping("rename")
    public Result collectRenamePath(@RequestBody @Valid CollectRenamePathDto renamePathDto){
        collectService.collectRenamePath(renamePathDto);
        return Result.OK();
    }

    @PostMapping("image/list")
    public Result<PageResult<CollectImageDetailVo>> qryCollectImageList(@RequestBody(required = false) CollectImageListDto listDto){
        PageResult<CollectImageDetailVo> list = collectService.qryCollectImageList(listDto);
        return Result.OK(list);
    }

//    @PostMapping("image/detail")
//    public Result<CollectImageDetailVo> qryCollectImageDetail(@RequestBody @Valid CollectImageDetailDto detailDto){
//        CollectImageDetailVo imageDetailVo = collectService.qryCollectImageDetail(detailDto);
//        return Result.OK(imageDetailVo);
//    }

    @PostMapping("image/add")
    public Result addCollectImage(@RequestBody @Valid CollectImageAddDto addDto){
        collectService.addCollectImage(addDto);
        return Result.OK();
    }

//    @PostMapping("image/addBatch")
//    public Result addCollectImage(@RequestBody @Valid CollectImageAddBatchDto addBatchDto){
//        collectService.addCollectImageBatch(addBatchDto);
//        return Result.OK();
//    }

    @PostMapping("image/remove")
    public Result removeCollectImage(@RequestBody @Valid CollectImageRemoveDto removeDto){
        collectService.removeCollectImage(removeDto);
        return Result.OK();
    }

    @PostMapping("image/removeBatch")
    public Result removeCollectImage(@RequestBody @Valid CollectImageRemoveBatchDto removeBatchDto){
        collectService.removeCollectImageBatch(removeBatchDto);
        return Result.OK();
    }


}
