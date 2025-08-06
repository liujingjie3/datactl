package com.zjlab.dataservice.modules.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.SubjectApplication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.ISubjectApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 应用表
 * @Author: jeecg-boot
 * @Date:   2023-11-06
 * @Version: V1.0
 */
@Api(tags="应用表")
@RestController
@RequestMapping("/subject/app")
@Slf4j
public class SubjectApplicationController extends JeecgController<SubjectApplication, ISubjectApplicationService> {
	@Autowired
	private ISubjectApplicationService subjectApplicationService;
	
	/**
	 * 分页列表查询
	 *
	 * @param subjectApplication
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "应用表-分页列表查询")
	@ApiOperation(value="应用表-分页列表查询", notes="应用表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SubjectApplication>> queryPageList(SubjectApplication subjectApplication,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<SubjectApplication> queryWrapper = QueryGenerator.initQueryWrapper(subjectApplication, req.getParameterMap());
		Page<SubjectApplication> page = new Page<SubjectApplication>(pageNo, pageSize);
		IPage<SubjectApplication> pageList = subjectApplicationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param subjectApplication
	 * @return
	 */
	@AutoLog(value = "应用表-添加")
	@ApiOperation(value="应用表-添加", notes="应用表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_application:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SubjectApplication subjectApplication) {
		subjectApplicationService.save(subjectApplication);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param subjectApplication
	 * @return
	 */
	@AutoLog(value = "应用表-编辑")
	@ApiOperation(value="应用表-编辑", notes="应用表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_application:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SubjectApplication subjectApplication) {
		subjectApplicationService.updateById(subjectApplication);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应用表-通过id删除")
	@ApiOperation(value="应用表-通过id删除", notes="应用表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_application:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		subjectApplicationService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "应用表-批量删除")
	@ApiOperation(value="应用表-批量删除", notes="应用表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_application:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.subjectApplicationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "应用表-通过id查询")
	@ApiOperation(value="应用表-通过id查询", notes="应用表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SubjectApplication> queryById(@RequestParam(name="id",required=true) String id) {
		SubjectApplication subjectApplication = subjectApplicationService.getById(id);
		if(subjectApplication==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(subjectApplication);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param subjectApplication
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_application:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SubjectApplication subjectApplication) {
        return super.exportXls(request, subjectApplication, SubjectApplication.class, "应用表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_application:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SubjectApplication.class);
    }

}
