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
import com.zjlab.dataservice.modules.tc.service.TcTaskManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.zjlab.dataservice.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;

import java.util.List;

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
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.FILE_NAME, "遥控指令单");
        mv.addObject(NormalExcelConstants.CLASS, RemoteCmdExportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("遥控指令单", "导出人:" + user.getRealname(), "遥控指令单"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    @PostMapping("/exportOrbitPlans")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "导出仿真轨道计划", notes = "导出前端传来的仿真轨道计划")
    public ModelAndView exportOrbitPlans(@RequestBody List<OrbitPlanExportVO> list) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.FILE_NAME, "测运控仿真轨道计划");
        mv.addObject(NormalExcelConstants.CLASS, OrbitPlanExportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS,
                new ExportParams("测运控仿真轨道计划", "导出人:" + user.getRealname(), "轨道计划"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    @GetMapping("/nodeFlows")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "查询模板节点流", notes = "根据模板ID获取节点流列表")
    public Result<List<TemplateNodeFlowVO>> nodeFlows(@RequestParam String templateId) {
        List<TemplateNodeFlowVO> flows = taskManagerService.listNodeFlows(templateId);
        return Result.ok(flows);
    }

    @PostMapping("/node/submit")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "提交节点操作", notes = "节点办理操作提交")
    public Result<Void> submitNodeAction(@RequestBody @Valid NodeActionSubmitDto dto) {
        taskManagerService.submitAction(dto);
        return Result.ok();
    }
}
