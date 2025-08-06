package com.zjlab.dataservice.modules.xxljob.service;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjlab.dataservice.common.util.HttpUtil;
import com.zjlab.dataservice.modules.screen.service.DataScreenService;
import com.zjlab.dataservice.modules.screen.service.RegionalStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ImageJobHandler {

    private static final String IP = "http://10.107.104.18:8088";
    //GF1、2、7 数据自动入库接口
    private static final String IMAGE_PROCESS_GF127 = "/dspp/tbx/prehandle/batchdb/gf2";
    //GF3数据自动入库接口
    private static final String IMAGE_PROCESS_GF3 = "/dspp//tbx/prehandle/batchdb/gf3";


    @Resource
    private DataScreenService dataScreenService;
    @Resource
    private RegionalStatisticsService statisticsService;

    @XxlJob("test")
    public ReturnT<String> testJobHandler(){
        log.info("========= execute job.");
        XxlJobHelper.log("{} execute testJobHandler success.", this.getClass().getSimpleName());
        return ReturnT.SUCCESS;
    }

    @XxlJob("imageProcessBatchdb")
    public ReturnT<String> imageProcess(){
        String url = IP + IMAGE_PROCESS_GF127;
        try {
            HttpUtil.get(url, null);
        }catch (Exception e){
            XxlJobHelper.log("{} execute imageProcess job failed. {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("imageProcessBatchdb3")
    public ReturnT<String> imageProcess3(){
        String url = IP + IMAGE_PROCESS_GF3;
        try {
            HttpUtil.get(url, null);
        }catch (Exception e){
            XxlJobHelper.log("{} execute imageProcess job failed. {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("fullOnceCoverageCalculation")
    public ReturnT<String> fullOnceCoverageCalculation(){
        try {
            XxlJobHelper.log("will calculate coverage.");
            long startTime = System.currentTimeMillis();
            statisticsService.initRegionalStatistics();
            XxlJobHelper.log("init regional statistics success");
            statisticsService.updateProvinceStatistics();
            XxlJobHelper.log("update province coverage success.");
            statisticsService.updateCityStatistics();
            XxlJobHelper.log("update city coverage success.");
            long endTime = System.currentTimeMillis();
            XxlJobHelper.log("calculate coverage success. cost time : {}", endTime - startTime);
            return ReturnT.SUCCESS;
        }catch (Exception e){
            XxlJobHelper.log("{} execute coverageCalculation job failed. {}", this.getClass().getSimpleName(), e.getMessage());
            return ReturnT.FAIL;
        }
    }

    @XxlJob("periodCoverageCalculation")
    public ReturnT<String> periodCoverageCalculation(){
        try {
            XxlJobHelper.log("will calculate coverage.");
            long startTime = System.currentTimeMillis();
            statisticsService.updateProvinceStatistics();
            XxlJobHelper.log("update province coverage success.");
            statisticsService.updateCityStatistics();
            XxlJobHelper.log("update city coverage success.");
            long endTime = System.currentTimeMillis();
            XxlJobHelper.log("calculate coverage success. cost time : {}", endTime - startTime);
            return ReturnT.SUCCESS;
        }catch (Exception e){
            XxlJobHelper.log("{} execute coverageCalculation job failed. {}", this.getClass().getSimpleName(), e.getMessage());
            XxlJobHelper.handleFail(e.getMessage());
            return ReturnT.FAIL;
        }
    }

}
