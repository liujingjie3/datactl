package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.*;
import com.zjlab.dataservice.modules.tc.service.TcNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 节点管理接口
 */
@RestController
@RequestMapping("/tc/node")
@Api(tags="节点管理")
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
        try {
            return Result.ok(tcNodeService.getStats());
        } catch (Exception e) {
            return Result.error("获取节点统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 查询节点列表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询节点列表")
    public Result<PageResult<NodeInfoDto>> list(NodeQueryDto queryDto) {
        try {
            return Result.ok(tcNodeService.listNodes(queryDto));
        } catch (Exception e) {
            return Result.error("查询节点列表失败: " + e.getMessage());
        }
    }

    /**
     * 新建节点
     */
    @PostMapping("/create")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "新建节点")
    public Result<Long> create(@RequestBody NodeInfoDto dto) {
        try {
            return Result.ok(tcNodeService.createNode(dto));
        } catch (Exception e) {
            return Result.error("新建节点失败: " + e.getMessage());
        }
    }

    /**
     * 编辑节点
     */
    @PutMapping("/update/{id}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "编辑节点")
    public Result<Void> update(@PathVariable Long id, @RequestBody NodeInfoDto dto) {
        try {
            tcNodeService.updateNode(id, dto);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("编辑节点失败: " + e.getMessage());
        }
    }

    /**
     * 更新节点状态
     */
    @PatchMapping("/status/{id}")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "更新节点状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            tcNodeService.updateStatus(id, status);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("更新节点状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除节点
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "删除节点")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            tcNodeService.deleteNode(id);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("删除节点失败: " + e.getMessage());
        }
    }

    /**
     * 获取节点详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "获取节点详情")
    public Result<NodeInfoDto> detail(@PathVariable Long id) {
        try {
            return Result.ok(tcNodeService.getDetail(id));
        } catch (Exception e) {
            return Result.error("获取节点详情失败: " + e.getMessage());
        }
    }
}
