package com.zjlab.dataservice.modules.backup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.backup.model.dto.TaskRecordDto;
import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
import com.zjlab.dataservice.modules.backup.model.vo.TaskRecordVo;

public interface TaskRecordService extends IService<TaskRecordPo> {

    PageResult<TaskRecordVo> qryTaskRecordList(TaskRecordDto recordDto);
}
