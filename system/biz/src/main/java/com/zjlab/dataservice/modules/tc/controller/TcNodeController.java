package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.service.TcNodeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 节点管理接口
 */
@RestController
@RequestMapping("/tc/node")
public class TcNodeController {

    @Autowired
    private TcNodeService tcNodeService;

    /**
     * 获取节点统计数据
     */
    @GetMapping("/stats")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取节点统计数据")
    public Result<NodeStatsVo> stats() {
        return Result.ok(tcNodeService.getStats());
    }

    /**
     * 查询节点列表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询节点列表")
    public Result<PageResult<NodeListDto>> list(NodeQueryDto queryDto) {
        return Result.ok(tcNodeService.listNodes(queryDto));
    }

    /**
     * 新建节点
     */
    @PostMapping("/create")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "新建节点")
    public Result<Long> create(@RequestBody NodeInfoDto dto) {
        return Result.ok(tcNodeService.createNode(dto));
    }

    /**
     * 编辑节点
     */
    @PutMapping("/update/{id}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "编辑节点")
    public Result<Void> update(@PathVariable Long id, @RequestBody NodeInfoDto dto) {
        tcNodeService.updateNode(id, dto);
        return Result.ok();
    }

    /**
     * 更新节点状态
     */
    @PatchMapping("/status/{id}")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "更新节点状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody NodeStatusDto dto) {
        tcNodeService.updateStatus(id, dto.getStatus());
        return Result.ok();
    }

    /**
     * 删除节点
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "删除节点")
    public Result<Void> delete(@PathVariable Long id) {
        tcNodeService.deleteNode(id);
        return Result.ok();
    }

    /**
     * 获取节点详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "获取节点详情")
    public Result<NodeDetailDto> detail(@PathVariable Long id) {
        return Result.ok(tcNodeService.getDetail(id));
    }
}
