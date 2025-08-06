package com.zjlab.dataservice.modules.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.SubjectNotice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.ISubjectNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 咨询表
 * @Author: jeecg-boot
 * @Date:   2023-10-16
 * @Version: V1.0
 */
@Api(tags="咨询表")
@RestController
@RequestMapping("/subjectNotice")
@Slf4j
public class SubjectNoticeController extends JeecgController<SubjectNotice, ISubjectNoticeService> {
	@Autowired
	private ISubjectNoticeService subjectNoticeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param subjectNotice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "咨询表-分页列表查询")
	@ApiOperation(value="咨询表-分页列表查询", notes="咨询表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SubjectNotice>> queryPageList(SubjectNotice subjectNotice,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		QueryWrapper<SubjectNotice> queryWrapper = QueryGenerator.initQueryWrapper(subjectNotice, req.getParameterMap());
		Page<SubjectNotice> page = new Page<SubjectNotice>(pageNo, pageSize);
		IPage<SubjectNotice> pageList = subjectNoticeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param subjectNotice
	 * @return
	 */
	@AutoLog(value = "咨询表-添加")
	@ApiOperation(value="咨询表-添加", notes="咨询表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_notice:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SubjectNotice subjectNotice) {
		subjectNoticeService.save(subjectNotice);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param subjectNotice
	 * @return
	 */
	@AutoLog(value = "咨询表-编辑")
	@ApiOperation(value="咨询表-编辑", notes="咨询表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_notice:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SubjectNotice subjectNotice) {
		subjectNoticeService.updateById(subjectNotice);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "咨询表-通过id删除")
	@ApiOperation(value="咨询表-通过id删除", notes="咨询表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_notice:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		subjectNoticeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "咨询表-批量删除")
	@ApiOperation(value="咨询表-批量删除", notes="咨询表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_notice:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.subjectNoticeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "咨询表-通过id查询")
	@ApiOperation(value="咨询表-通过id查询", notes="咨询表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SubjectNotice> queryById(@RequestParam(name="id",required=true) String id) {
		SubjectNotice subjectNotice = subjectNoticeService.getById(id);
		if(subjectNotice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(subjectNotice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param subjectNotice
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_notice:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SubjectNotice subjectNotice) {
        return super.exportXls(request, subjectNotice, SubjectNotice.class, "咨询表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_notice:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SubjectNotice.class);
    }

}
