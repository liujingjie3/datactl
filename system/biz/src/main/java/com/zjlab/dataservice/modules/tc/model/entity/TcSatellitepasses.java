package com.zjlab.dataservice.modules.tc.model.entity;


import com.zjlab.dataservice.common.system.base.entity.BasePo;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * 创建任务时卫星轨迹信息存储
 * @TableName tc_satellitepasses
 */
public class TcSatellitepasses extends BasePo {

    /**
     * id，自增
     */
    private Integer id;
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 该圈子数据在PG数据库里的id
     */
    private Integer dataId;
    /**
     * 进站时间
     */
    private LocalDateTime aos;
    /**
     * 出站时间
     */
    private LocalDateTime los;
    /**
     * 卫星代号
     */
    private String sat;
    /**
     * 地面站信息
     */
    private String station;
    /**
     * 时长
     */
    private Integer duration;



}
