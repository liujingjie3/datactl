package com.zjlab.dataservice.modules.tc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerCreateDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerEditDto;
import com.zjlab.dataservice.modules.tc.model.dto.TaskManagerListQuery;
import com.zjlab.dataservice.modules.tc.model.dto.NodeActionSubmitDto;
import com.zjlab.dataservice.modules.tc.model.vo.TaskManagerListItemVO;
import com.zjlab.dataservice.modules.tc.model.vo.RemoteCmdExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.OrbitPlanExportVO;
import com.zjlab.dataservice.modules.tc.model.vo.TemplateNodeFlowVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskDetailVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskCountVO;
import com.zjlab.dataservice.modules.tc.model.vo.TaskNodeActionVO;
import com.alibaba.fastjson.JSON;
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
/** 任务列表接口 */
@RestController
@RequestMapping("/tc/taskManager")
@Api(tags="任务管理")
@Validated
public class TcTaskManagerController {

    @Autowired
    private TcTaskManagerService taskManagerService;

    @GetMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value="任务列表", notes="分页查询任务列表")
    public Result<PageResult<TaskManagerListItemVO>> list(@Valid TaskManagerListQuery query) {
        return Result.ok(taskManagerService.listTasks(query));
    }

    @GetMapping("/detail")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "任务详情", notes = "根据任务ID查询任务详情")
    public Result<TaskDetailVO> detail(@RequestParam Long taskId) {
        // 调用服务层组装任务详情并返回
        return Result.ok(taskManagerService.getTaskDetail(taskId));
    }

    @PostMapping("/create")
    @ApiOperationSupport(order = 0)
    @ApiOperation(value = "创建任务", notes = "创建新的任务")
    public Result<Long> create(@RequestBody @Valid TaskManagerCreateDto dto) {
        Long id = taskManagerService.createTask(dto);
        return Result.ok(id);
    }

    @PostMapping("/cancel")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value="取消任务", notes="取消指定任务")
    public Result<Void> cancel(@RequestParam Long taskId) {
        taskManagerService.cancelTask(taskId);
        return Result.ok();
    }

    @PostMapping("/edit")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "编辑任务", notes = "编辑指定任务")
    public Result<Void> edit(@RequestBody @Valid TaskManagerEditDto dto) {
        taskManagerService.editTask(dto);
        return Result.ok();
    }

    @PostMapping("/exportRemoteCmds")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "导出遥控指令单", notes = "导出前端传来的遥控指令单")
    public ModelAndView exportRemoteCmds(@RequestBody List<RemoteCmdExportVO> list) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "遥控指令单");
        mv.addObject(NormalExcelConstants.CLASS, RemoteCmdExportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("遥控指令单", "遥控指令单"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    @PostMapping("/exportOrbitPlans")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "导出仿真轨道计划", notes = "导出前端传来的仿真轨道计划")
    public ModelAndView exportOrbitPlans(@RequestBody List<OrbitPlanExportVO> list) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "测运控仿真轨道计划");
        mv.addObject(NormalExcelConstants.CLASS, OrbitPlanExportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("测运控仿真轨道计划", "轨道计划"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    @GetMapping("/remoteCmds")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "查询遥控指令单", notes = "根据任务ID查询遥控指令单列表")
    public Result<List<RemoteCmdExportVO>> getRemoteCmds(@RequestParam Long taskId) {
        return Result.ok(taskManagerService.getRemoteCmds(taskId));
    }

    @GetMapping("/orbitPlans")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "查询轨道计划", notes = "根据任务ID查询轨道计划列表")
    public Result<List<OrbitPlanExportVO>> getOrbitPlans(@RequestParam Long taskId) {
        return Result.ok(taskManagerService.getOrbitPlans(taskId));
    }

    @GetMapping("/orbitPlans/realtime")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "查询实时轨道计划", notes = "调用测运控接口实时获取轨道计划")
    public Result<List<OrbitPlanExportVO>> getOrbitPlansRealtime() {
        // TODO 调用测运控接口实时计算轨道计划
        return Result.ok(taskManagerService.getRealtimeOrbitPlans());
    }

    @GetMapping("/nodeFlows")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "查询模板节点流", notes = "根据模板ID获取节点流列表")
    public Result<List<TemplateNodeFlowVO>> nodeFlows(@RequestParam String templateId,
                                                     @RequestParam Integer resultDisplayNeeded) {
        List<TemplateNodeFlowVO> flows = taskManagerService.listNodeFlows(templateId, resultDisplayNeeded);
        return Result.ok(flows);
    }

    @PostMapping(value = "/node/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "提交节点操作", notes = "节点办理操作提交")
    public Result<Void> submitNodeAction(@RequestParam Long taskId,
                                         @RequestParam Long nodeInstId,
                                         @RequestParam String actions,
                                         @RequestParam(value = "files", required = false) MultipartFile[] files) {
        NodeActionSubmitDto dto = new NodeActionSubmitDto();
        dto.setTaskId(taskId);
        dto.setNodeInstId(nodeInstId);
        dto.setActions(JSON.parseArray(actions, TaskNodeActionVO.class));
        taskManagerService.submitAction(dto, files);
        return Result.ok();
    }

    @GetMapping("/statistics")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "任务统计", notes = "获取总任务数、运行中、已完成和已取消任务数")
    public Result<TaskCountVO> statistics() {
        return Result.ok(taskManagerService.countTasks());
    }

    @GetMapping("/download")
    @ApiOperationSupport(order = 13)
    @ApiOperation(value = "下载节点附件", notes = "根据附件URL下载文件")
    public void download(@RequestParam String url, HttpServletResponse response) {
        taskManagerService.downloadAttachment(url, response);
    }
}
