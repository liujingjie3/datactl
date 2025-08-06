package com.zjlab.dataservice.modules.screen.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataRegionStatisticsVo {

    private String regionCode;

    private String regionName;

    private long GF2;

    private long GF3;

    private String timeCoverage;

    private String spaceCoverage;

    private long imageCount;

    private String imageMode;

    public DataRegionStatisticsVo(){

    }
    public DataRegionStatisticsVo(String regionCode, String regionName, long gf2, long gf3, String timeCoverage, String spaceCoverage){
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.GF2 = gf2;
        this.GF3 = gf3;
        this.imageCount = gf2 + gf3;
        if (gf2 > 0 && gf3 > 0){
            this.imageMode = "光学/SAR";
        } else if (gf2 > 0) {
            this.imageMode = "光学";
        }else {
            this.imageMode = "SAR";
        }
        this.timeCoverage = timeCoverage;
        this.spaceCoverage = spaceCoverage;
    }

    public DataRegionStatisticsVo(String regionCode, String regionName, long gf2, long gf3, String spaceCoverage){
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.GF2 = gf2;
        this.GF3 = gf3;
        this.imageCount = gf2 + gf3;
        if (gf2 > 0 && gf3 > 0){
            this.imageMode = "光学/SAR";
        } else if (gf2 > 0) {
            this.imageMode = "光学";
        }else {
            this.imageMode = "SAR";
        }
        this.spaceCoverage = spaceCoverage;
    }
}
