package com.zjlab.dataservice.modules.tc.service;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.model.vo.NodeStatsVO;

/**
 * 节点管理服务
 */
public interface TcNodeService {
    /**
     * 统计节点数量
     *
     * @return 活跃/禁用及总数
     */
    NodeStatsVO getStats();

    /**
     * 分页查询节点列表
     *
     * @param queryDto 查询条件
     * @return 节点分页结果
     */
    PageResult<NodeInfoDto> listNodes(NodeQueryDto queryDto);

    /**
     * 创建节点
     *
     * @param dto 节点信息
     * @return 新节点ID
     */
    Long createNode(NodeInfoDto dto);

    /**
     * 更新节点及角色关系
     *
     * @param id  节点ID
     * @param dto 节点信息
     */
    void updateNode(Long id, NodeInfoDto dto);

    /**
     * 更新节点状态
     *
     * @param id     节点ID
     * @param status 状态值
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除节点
     *
     * @param id 节点ID
     */
    void deleteNode(Long id);

    /**
     * 查询节点详情
     *
     * @param id 节点ID
     * @return 节点详细信息
     */
    NodeInfoDto getDetail(Long id);
}
