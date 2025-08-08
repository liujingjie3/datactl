package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.*;

public interface TcNodeService {
    NodeStatsVo getStats();

    PageResult<NodeListDto> listNodes(NodeQueryDto queryDto);

    Long createNode(NodeCreateOrUpdateDto dto);

    void updateNode(Long id, NodeCreateOrUpdateDto dto);

    void updateStatus(Long id, Integer status);

    void deleteNode(Long id);

    NodeDetailDto getDetail(Long id);
}
