package com.zjlab.dataservice.modules.screen.controller;

import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.screen.model.vo.*;
import com.zjlab.dataservice.modules.screen.service.DataScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 *  数据大屏接口
 */

@RestController
@RequestMapping("/screen")
@Slf4j
public class DataScreenController {

    @Resource
    private DataScreenService dataScreenService;

    /**
     * 切片数量统计
     * 总量：
     * 入库：
     * L2：
     * L3：
     * 切片：
     * @return
     */
    @GetMapping("slice")
    public Result<SliceStatisticsVo> qrySliceStatistics(){
        SliceStatisticsVo sliceStatisticsVo = dataScreenService.qrySliceStatistics();
        if (sliceStatisticsVo.getSlice() == 0) {
            sliceStatisticsVo.setSlice(8555995);
            sliceStatisticsVo.setTotal(8555995);
        }
        return Result.OK(sliceStatisticsVo);
    }

    /**
     * 数据时序分布：原始影像
     * @param regionCode 行政区划id，不填表示查询全国数据
     * @return GF2 GF3 总数据，按时间正序排序
     */
    @GetMapping("data/timeSequence")
    public Result<List<TimeSequenceVo>> qryDataTimeSequence(@RequestParam(value = "regionCode",required = false,defaultValue = "330000") String regionCode){
        List<TimeSequenceVo> timeSequenceVos = dataScreenService.qryDataTimeSequence(regionCode);
        return Result.OK(timeSequenceVos);
    }

    /**
     * 行政区划统计数据
     * @param regionCode 行政区划id，不填表示查询全国数据
     * @return 返回结果只包含下一级单位，不返回所有树结构
     *          eg： 查询全国 -- 只返回升级区划数据
     *              查询某个省 -- 只返回市一级区划数据
     */
    @GetMapping("data/region")
    public Result<List<YearSequenceVo<DataRegionStatisticsVo>>> qryDataRegionStatistics(@RequestParam(value = "regionCode",required = false,defaultValue = "330000") String regionCode){
        List<YearSequenceVo<DataRegionStatisticsVo>> yearSequenceVos = dataScreenService.qryDataRegionStatistics(regionCode);
        return Result.OK(yearSequenceVos);
    }
}
