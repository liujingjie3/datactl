package com.zjlab.dataservice.modules.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.SujectProject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.ISujectProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 专题项目表
 * @Author: jeecg-boot
 * @Date:   2023-11-29
 * @Version: V1.0
 */
@Api(tags="专题项目表")
@RestController
@RequestMapping("/subject/project")
@Slf4j
public class SujectProjectController extends JeecgController<SujectProject, ISujectProjectService> {
	@Autowired
	private ISujectProjectService sujectProjectService;
	
	/**
	 * 分页列表查询
	 *
	 * @param sujectProject
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "专题项目表-分页列表查询")
	@ApiOperation(value="专题项目表-分页列表查询", notes="专题项目表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SujectProject>> queryPageList(SujectProject sujectProject,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		QueryWrapper<SujectProject> queryWrapper = QueryGenerator.initQueryWrapper(sujectProject, req.getParameterMap());
		Page<SujectProject> page = new Page<SujectProject>(pageNo, pageSize);
		IPage<SujectProject> pageList = sujectProjectService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param sujectProject
	 * @return
	 */
	@AutoLog(value = "专题项目表-添加")
	@ApiOperation(value="专题项目表-添加", notes="专题项目表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:suject_project:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SujectProject sujectProject) {
		sujectProjectService.save(sujectProject);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param sujectProject
	 * @return
	 */
	@AutoLog(value = "专题项目表-编辑")
	@ApiOperation(value="专题项目表-编辑", notes="专题项目表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:suject_project:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SujectProject sujectProject) {
		sujectProjectService.updateById(sujectProject);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "专题项目表-通过id删除")
	@ApiOperation(value="专题项目表-通过id删除", notes="专题项目表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:suject_project:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sujectProjectService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "专题项目表-批量删除")
	@ApiOperation(value="专题项目表-批量删除", notes="专题项目表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:suject_project:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sujectProjectService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "专题项目表-通过id查询")
	@ApiOperation(value="专题项目表-通过id查询", notes="专题项目表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SujectProject> queryById(@RequestParam(name="id",required=true) String id) {
		SujectProject sujectProject = sujectProjectService.getById(id);
		if(sujectProject==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sujectProject);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sujectProject
    */
    //@RequiresPermissions("org.jeecg.modules.demo:suject_project:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SujectProject sujectProject) {
        return super.exportXls(request, sujectProject, SujectProject.class, "专题项目表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("suject_project:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SujectProject.class);
    }

}
