package com.zjlab.dataservice.modules.tc.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * 
 * @TableName satellite_passes
 */
@Data
@TableName(value = "satellite_passes")
public class SatellitePasses implements Serializable {

    private Integer id;
    private String sat;
    private String station;
    private LocalDateTime aos;
    private LocalDateTime los;
    private Double maxElevDeg;
    private Double maxAzDeg;
    private Date maxElevTime;
    private Double aosAltDeg;
    private Double aosAzDeg;
    private Double aosDis;
    private Double losAltDeg;
    private Double losAzDeg;
    private Double losDis;
    private Integer durationS;
    private String duration;
    private Boolean daylight;
    private Double dopplerHzAtAos;
    private Double dopplerHzAtTca;
    private Double dopplerHzAtLos;

}
