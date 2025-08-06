package com.zjlab.dataservice.modules.subject.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.SubjectWorkflow;
import com.zjlab.dataservice.modules.subject.vo.SubjectWorkflowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.ISubjectWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 工作流表
 * @Author: jeecg-boot
 * @Date:   2023-12-05
 * @Version: V1.0
 */
@Api(tags="工作流表")
@RestController
@RequestMapping("/subject/workflow")
@Slf4j
public class SubjectWorkflowController extends JeecgController<SubjectWorkflow, ISubjectWorkflowService> {
	@Autowired
	private ISubjectWorkflowService subjectWorkflowService;
	
	/**
	 * 分页列表查询
	 *
	 * @param subjectWorkflow
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "工作流表-分页列表查询")
	@ApiOperation(value="工作流表-分页列表查询", notes="工作流表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SubjectWorkflow>> queryPageList(SubjectWorkflow subjectWorkflow,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		QueryWrapper<SubjectWorkflow> queryWrapper = QueryGenerator.initQueryWrapper(subjectWorkflow, req.getParameterMap());
		Page<SubjectWorkflow> page = new Page<SubjectWorkflow>(pageNo, pageSize);
		IPage<SubjectWorkflow> pageList = subjectWorkflowService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param subjectWorkflowVO
	 * @return
	 */
	@AutoLog(value = "工作流表-添加")
	@ApiOperation(value="工作流表-添加", notes="工作流表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_workflow:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SubjectWorkflowVO subjectWorkflowVO) {
		SubjectWorkflow subjectWorkflow = new SubjectWorkflow();
		BeanUtil.copyProperties(subjectWorkflowVO,subjectWorkflow);
		if (ObjectUtil.isNotEmpty(subjectWorkflowVO.getFlowDataVo())){
			subjectWorkflow.setFlowData(subjectWorkflowVO.getFlowDataVo().toString());
		}

		subjectWorkflowService.save(subjectWorkflow);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param subjectWorkflowVO
	 * @return
	 */
	@AutoLog(value = "工作流表-编辑")
	@ApiOperation(value="工作流表-编辑", notes="工作流表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_workflow:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SubjectWorkflowVO subjectWorkflowVO) {
		SubjectWorkflow subjectWorkflow = new SubjectWorkflow();
		BeanUtil.copyProperties(subjectWorkflowVO,subjectWorkflow);
		if (ObjectUtil.isNotEmpty(subjectWorkflowVO.getFlowDataVo())){
			subjectWorkflow.setFlowData(subjectWorkflowVO.getFlowDataVo().toString());
		}
		subjectWorkflowService.updateById(subjectWorkflow);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工作流表-通过id删除")
	@ApiOperation(value="工作流表-通过id删除", notes="工作流表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_workflow:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		subjectWorkflowService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工作流表-批量删除")
	@ApiOperation(value="工作流表-批量删除", notes="工作流表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_workflow:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.subjectWorkflowService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工作流表-通过id查询")
	@ApiOperation(value="工作流表-通过id查询", notes="工作流表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SubjectWorkflow> queryById(@RequestParam(name="id",required=true) String id) {
		SubjectWorkflow subjectWorkflow = subjectWorkflowService.getById(id);

		SubjectWorkflowVO workFlowVO = new SubjectWorkflowVO();
		BeanUtil.copyProperties(subjectWorkflow,workFlowVO);
		if (ObjectUtil.isNotEmpty(subjectWorkflow.getFlowData())){
			workFlowVO.setFlowDataVo(JSON.parseObject(subjectWorkflow.getFlowData()));
		}




		if(subjectWorkflow==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(subjectWorkflow);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param subjectWorkflow
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_workflow:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SubjectWorkflow subjectWorkflow) {
        return super.exportXls(request, subjectWorkflow, SubjectWorkflow.class, "工作流表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_workflow:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SubjectWorkflow.class);
    }

}
