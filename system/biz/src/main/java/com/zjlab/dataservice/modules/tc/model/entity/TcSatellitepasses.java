package com.zjlab.dataservice.modules.tc.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * 创建任务时卫星轨迹信息存储
 * @TableName tc_satellitepasses
 */
@Data
@TableName("tc_satellitePasses")
public class TcSatellitepasses extends BasePo {

    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 圈次信息
     */
    private String orbitNo;
    /**
     * 进站时间
     */
    private LocalDateTime inTime;
    /**
     * 出站时间
     */
    private LocalDateTime outTime;
    /**
     * 卫星代号
     */
    private String satelliteCode;
    /**
     * 地面站信息
     */
    private String groundStation;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 时长
     */
    private Integer duration;
    /**
     * 任务描述
     */
    private String task;


}
