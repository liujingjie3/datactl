package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.OperationLogDto;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetOperationLogPo;

import java.util.List;

public interface OperationLogService extends IService<DatasetOperationLogPo> {

    List<String> operationLog(OperationLogDto operationLogDto);
}
