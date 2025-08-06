package com.zjlab.dataservice.modules.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.SubjectTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.ISubjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 专题任务表
 * @Author: jeecg-boot
 * @Date:   2023-11-27
 * @Version: V1.0
 */
@Api(tags="专题任务表")
@RestController
@RequestMapping("/subject/task")
@Slf4j
public class SubjectTaskController extends JeecgController<SubjectTask, ISubjectTaskService> {
	@Autowired
	private ISubjectTaskService subjectTaskService;
	
	/**
	 * 分页列表查询
	 *
	 * @param subjectTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "专题任务表-分页列表查询")
	@ApiOperation(value="专题任务表-分页列表查询", notes="专题任务表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SubjectTask>> queryPageList(SubjectTask subjectTask,
													@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													HttpServletRequest req) {
		QueryWrapper<SubjectTask> queryWrapper = QueryGenerator.initQueryWrapper(subjectTask, req.getParameterMap());
		Page<SubjectTask> page = new Page<SubjectTask>(pageNo, pageSize);
		IPage<SubjectTask> pageList = subjectTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param subjectTask
	 * @return
	 */
	@AutoLog(value = "专题任务表-添加")
	@ApiOperation(value="专题任务表-添加", notes="专题任务表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_task:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SubjectTask subjectTask) {
		subjectTaskService.save(subjectTask);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param subjectTask
	 * @return
	 */
	@AutoLog(value = "专题任务表-编辑")
	@ApiOperation(value="专题任务表-编辑", notes="专题任务表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_task:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SubjectTask subjectTask) {
		subjectTaskService.updateById(subjectTask);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "专题任务表-通过id删除")
	@ApiOperation(value="专题任务表-通过id删除", notes="专题任务表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_task:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		subjectTaskService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "专题任务表-批量删除")
	@ApiOperation(value="专题任务表-批量删除", notes="专题任务表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_task:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.subjectTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "专题任务表-通过id查询")
	@ApiOperation(value="专题任务表-通过id查询", notes="专题任务表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SubjectTask> queryById(@RequestParam(name="id",required=true) String id) {
		SubjectTask subjectTask = subjectTaskService.getById(id);
		if(subjectTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(subjectTask);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param subjectTask
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SubjectTask subjectTask) {
        return super.exportXls(request, subjectTask, SubjectTask.class, "专题任务表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_task:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SubjectTask.class);
    }

}
