package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.FlowTypeDto;


public interface TcFlowTypeService {

    PageResult<FlowTypeDto> findAllTypeName();
}
