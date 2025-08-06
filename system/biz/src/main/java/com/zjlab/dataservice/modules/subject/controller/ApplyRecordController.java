package com.zjlab.dataservice.modules.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.ApplyRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.IApplyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 记录申请的表格
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Api(tags="记录申请的表格")
@RestController
@RequestMapping("/subject/applyRecord")
@Slf4j
public class ApplyRecordController extends JeecgController<ApplyRecord, IApplyRecordService> {
	@Autowired
	private IApplyRecordService applyRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param applyRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "记录申请的表格-分页列表查询")
	@ApiOperation(value="记录申请的表格-分页列表查询", notes="记录申请的表格-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ApplyRecord>> queryPageList(ApplyRecord applyRecord,
													@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													HttpServletRequest req) {
		QueryWrapper<ApplyRecord> queryWrapper = QueryGenerator.initQueryWrapper(applyRecord, req.getParameterMap());
		Page<ApplyRecord> page = new Page<ApplyRecord>(pageNo, pageSize);
		IPage<ApplyRecord> pageList = applyRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
//	/**
//	 *   添加
//	 *
//	 * @param applyRecord
//	 * @return
//	 */
//	@AutoLog(value = "记录申请的表格-添加")
//	@ApiOperation(value="记录申请的表格-添加", notes="记录申请的表格-添加")
//	//@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:add")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody ApplyRecord applyRecord) {
//		applyRecordService.save(applyRecord);
//		return Result.OK("添加成功！");
//	}







	
	/**
	 *  编辑
	 *
	 * @param applyRecord
	 * @return
	 */
	@AutoLog(value = "记录申请的表格-编辑")
	@ApiOperation(value="记录申请的表格-编辑", notes="记录申请的表格-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ApplyRecord applyRecord) {
		applyRecordService.updateById(applyRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "记录申请的表格-通过id删除")
	@ApiOperation(value="记录申请的表格-通过id删除", notes="记录申请的表格-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		applyRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "记录申请的表格-批量删除")
	@ApiOperation(value="记录申请的表格-批量删除", notes="记录申请的表格-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.applyRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "记录申请的表格-通过id查询")
	@ApiOperation(value="记录申请的表格-通过id查询", notes="记录申请的表格-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ApplyRecord> queryById(@RequestParam(name="id",required=true) String id) {
		ApplyRecord applyRecord = applyRecordService.getById(id);
		if(applyRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(applyRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param applyRecord
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ApplyRecord applyRecord) {
        return super.exportXls(request, applyRecord, ApplyRecord.class, "记录申请的表格");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_apply_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ApplyRecord.class);
    }

}
