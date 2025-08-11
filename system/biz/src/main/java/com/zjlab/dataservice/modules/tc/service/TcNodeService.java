package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.*;

public interface TcNodeService {
    NodeStatsVo getStats();

    PageResult<NodeInfoDto> listNodes(NodeQueryDto queryDto);

    Long createNode(NodeInfoDto dto);

    void updateNode(Long id, NodeInfoDto dto);

    void updateStatus(Long id, Integer status);

    void deleteNode(Long id);

    NodeInfoDto getDetail(Long id);
}
