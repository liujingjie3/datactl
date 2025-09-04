package com.zjlab.dataservice.modules.taskplan.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.io.Serializable;

/**
 * 预规划数据管理表
 * @TableName tc_cxtask
 */
@Data
@TableName(value = "tc_cxtask")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TcCxtaskPo extends BasePo {

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务申请用途
     */
    private String taskInfo;
    /**
     * 影像级别
     */
    private String level;
    /**
     * 卫星列表
     */
    private String satelliteInfo;
    /**
     * 成像指令列表
     */
    private String comandList;
    private Integer from; //1为任务中心，2为成像系统
    /**
     * 任务状态：1.已经 2.失败 3.已完成
     */
    private Integer status;

}
