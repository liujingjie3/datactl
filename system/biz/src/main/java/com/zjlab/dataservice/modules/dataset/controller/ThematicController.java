package com.zjlab.dataservice.modules.dataset.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.dataset.factory.HeadInfo;
import com.zjlab.dataservice.modules.dataset.factory.HeadInfoFactory;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataDelDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicDataListDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicEditDto;
import com.zjlab.dataservice.modules.dataset.model.dto.thematic.ThematicMapDto;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicDataPo;
import com.zjlab.dataservice.modules.dataset.model.po.ThematicJZHPo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.StaticsVo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicListVo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicMapVo;
import com.zjlab.dataservice.modules.dataset.model.vo.thematic.ThematicTypeVo;
import com.zjlab.dataservice.modules.dataset.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/thematic")
@Slf4j
public class ThematicController {

    @Resource
    private ThematicDataService thematicDataService;

    @Resource
    private ThematicTypesService thematicTypeService;

    @Resource
    private ThematicMapService thematicService;

    //专题数据接口
    @PostMapping("list")
    public Result<PageResult<ThematicListVo>> qryThematicDataList(@Valid @RequestBody ThematicDataListDto thematicDataListDto) {
        PageResult<ThematicListVo> thematicList = thematicDataService.qryThematicDataList(thematicDataListDto);
        return Result.OK(thematicList);
    }

    @GetMapping("info")
    public Result<ThematicDataPo> queryById(@RequestParam(name = "id") Integer id) {
        ThematicDataPo thematicDataPo = thematicDataService.getById(id);
        return Result.OK(thematicDataPo);
    }

    @PostMapping("del")
    public Result delById(@Valid @RequestBody ThematicDataDelDto thematicDataDelDto) {
        Integer del = thematicDataService.delThematicData(thematicDataDelDto.getId());
        return Result.OK(del);
    }

    @PostMapping("edit")
    public Result editThematicData(@Valid @RequestBody ThematicEditDto thematicEditDto) {
        String edit = thematicDataService.editThematicData(thematicEditDto);
        return Result.OK("", edit);
    }

    //专题类型接口
    @PostMapping("type")
    public Result<List<ThematicTypeVo>> qryThematicDataList(@RequestBody @Valid ThematicMapDto thematicMapDto) {
        ThematicDataPo thematicDataPo = thematicDataService.getById(thematicMapDto.getId());
        Integer type = thematicDataPo.getProductType();
        List<ThematicTypeVo> thematicTypeList = thematicTypeService.qryThematicType(type);
        return Result.OK(thematicTypeList);
    }

    @PostMapping("/map")
    public Result<ThematicMapVo> getGroupedMapByValue(@RequestBody @Valid ThematicMapDto thematicMapDto) {
        ThematicDataPo thematicDataPo = thematicDataService.getById(thematicMapDto.getId());
        //类别
        List<ThematicTypeVo> thematicTypeVos = thematicTypeService.qryThematicType(thematicDataPo.getProductType());
        Integer pac = thematicDataPo.getPac();
        List<ThematicJZHPo> ThematicPos = thematicService.getEntitiesByType(pac, thematicTypeVos);
        ThematicMapVo thematicMapVo = new ThematicMapVo();
        thematicMapVo.setThematicMap(ThematicPos);
        thematicMapVo.setThematicTypes(thematicTypeVos);
        return Result.OK(thematicMapVo);
    }

    @PostMapping("/pagemap")
    public Result<PageResult<ThematicJZHPo>> getGroupedMapByValue1(@RequestBody @Valid ThematicMapDto thematicMapDto) {
        ThematicDataPo thematicDataPo = thematicDataService.getById(thematicMapDto.getId());
        //类别
        List<ThematicTypeVo> thematicTypeVos = thematicTypeService.qryThematicType(thematicDataPo.getProductType());
        Integer pac = thematicDataPo.getPac();
        PageResult<ThematicJZHPo> ThematicPos = thematicService.getEntitiesByTypePage(pac, thematicMapDto, thematicTypeVos);
        return Result.OK(ThematicPos);
    }

    @PostMapping("/statistics")
    public Result<StaticsVo> statistics(@RequestBody @Valid ThematicMapDto thematicMapDto) {
        ThematicDataPo thematicDataPo = thematicDataService.getById(thematicMapDto.getId());
        //类别
        List<ThematicTypeVo> thematicTypeVos = thematicTypeService.qryThematicType(thematicDataPo.getProductType());
        Integer pac = thematicDataPo.getPac();
        List<StatisticsEntity> statistics = thematicService.statistics(pac, thematicTypeVos);
        StaticsVo staticsVo = new StaticsVo();
        List<HeadInfo> headInfoList = HeadInfoFactory.createHeadInfoList();
        staticsVo.setHead(headInfoList);
        staticsVo.setData(statistics);
        return Result.OK(staticsVo);
    }
}
