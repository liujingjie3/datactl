package com.zjlab.dataservice.modules.tc.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zjlab.dataservice.common.system.base.entity.BasePo;
import lombok.Data;

import java.io.Serializable;

@TableName("tc_flow_type")
@Data
public class FlowType extends BasePo implements Serializable {

    private String parentCode;

    private String typeCode;

    private String typeName;

    private Integer sort;

    private String typeDesc;

    private Integer enableFlag;

}
