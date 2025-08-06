package com.zjlab.dataservice.modules.backup.model.dto;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
//@Accessors(chain = true)
public class TaskRecordDto extends PageRequest {

    //过期时间(天)
    private Integer expireDay;
    //源数据类型
    private String sourceType;
    //目的数据类型
    private String destType;
    //执行状态：待执行，执行中，停止中，已结束
    private String status;
    //子状态：成功，失败，人工终止
    private String subStatus;
}
