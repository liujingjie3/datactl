package com.zjlab.dataservice.modules.subject.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.modules.subject.entity.UserTeam;
import com.zjlab.dataservice.modules.subject.vo.UserTeamVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.IUserTeamService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 用户对应团队表
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Api(tags="成员管理（用户对应团队表）")
@RestController
@RequestMapping("/subject/userTeam")
@Slf4j
public class UserTeamController extends JeecgController<UserTeam, IUserTeamService> {
	@Autowired
	private IUserTeamService userTeamService;
	@Autowired
	private ISysUserService sysUserService;
	
	/**
	 * 分页列表查询
	 *
	 * @param userTeam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户对应团队表-分页列表查询")
	@ApiOperation(value="团队成员查询", notes="团队成员查询")
	@GetMapping(value = "/list")
	public Result<IPage<UserTeam>> queryPageList(UserTeam userTeam,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		QueryWrapper<UserTeam> queryWrapper = QueryGenerator.initQueryWrapper(userTeam, req.getParameterMap());
		Page<UserTeam> page = new Page<UserTeam>(pageNo, pageSize);
		IPage<UserTeam> pageList = userTeamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}







	
	/**
	 *   添加
	 *
	 * @param userTeam
	 * @return
	 */
	@AutoLog(value = "用户对应团队表-添加")
	@ApiOperation(value="用户对应团队表-添加", notes="用户对应团队表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UserTeam userTeam) {
		userTeamService.save(userTeam);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param userTeam
	 * @return
	 */
	@AutoLog(value = "用户对应团队表-编辑")
	@ApiOperation(value="用户对应团队表-编辑", notes="用户对应团队表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UserTeam userTeam) {
		userTeamService.updateById(userTeam);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户对应团队表-通过id删除")
	@ApiOperation(value="用户对应团队表-通过id删除", notes="用户对应团队表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		userTeamService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户对应团队表-批量删除")
	@ApiOperation(value="用户对应团队表-批量删除", notes="用户对应团队表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.userTeamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}




	/**
	 *  批量删除
	 * @return
	 */
	@AutoLog(value = "删除成员")
	@ApiOperation(value="删除成员", notes="删除成员")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:deleteBatch")
	@PostMapping(value = "/deleteByUser")
	public Result<String> deleteByUser(@RequestBody UserTeamVO userTeam) {
		this.userTeamService.remove(new LambdaQueryWrapper<UserTeam>().in(UserTeam::getUserId,userTeam.getUserIdList()).eq(UserTeam::getTeamId,userTeam.getTeamId()));
		return Result.OK("删除成功!");
	}


	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户对应团队表-通过id查询")
	@ApiOperation(value="用户对应团队表-通过id查询", notes="用户对应团队表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UserTeam> queryById(@RequestParam(name="id",required=true) String id) {
		UserTeam userTeam = userTeamService.getById(id);
		if(userTeam==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(userTeam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param userTeam
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_user_team:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UserTeam userTeam) {
        return super.exportXls(request, userTeam, UserTeam.class, "用户对应团队表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_user_team:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UserTeam.class);
    }





	/**
	 * 通过team_id查询所有用户
	 *
	 * @param
	 * @return
	 */
	//@AutoLog(value = "用户对应团队表-通过id查询")
	@ApiOperation(value="查询团队用户", notes="查询团队用户")
	@GetMapping(value = "/queryUser")
	public Result<IPage<SysUser>> queryUser(UserTeam userTeam,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		QueryWrapper<UserTeam> queryWrapper = QueryGenerator.initQueryWrapper(userTeam, req.getParameterMap());
		Page<UserTeam> page = new Page<UserTeam>(pageNo, pageSize);
		IPage<UserTeam> pageList = userTeamService.page(page, queryWrapper);
		Page<SysUser> sysUserPage = new Page<>();
		BeanUtil.copyProperties(pageList,sysUserPage);
		if (pageList.getRecords().size() != 0) {
			List<String> userIds = new ArrayList<>();
			for (int i = 0; i < pageList.getRecords().size(); i++) {
				userIds.add(pageList.getRecords().get(i).getUserId());
			}
			String userIdStr = StrUtil.join(",", userIds);
			LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
			sysUserLambdaQueryWrapper.in(StrUtil.isNotBlank(userIdStr),SysUser::getId, userIds);
			if (ObjectUtil.isNotEmpty(req.getParameterMap().get("userName"))){
				sysUserLambdaQueryWrapper.like(StrUtil.isNotEmpty(req.getParameterMap().get("userName")[0]),SysUser::getUsername,  req.getParameterMap().get("userName")[0]);
			}

			List<SysUser> list = sysUserService.list(sysUserLambdaQueryWrapper);
			sysUserPage.setRecords(list);
		}else {
			sysUserPage.setRecords(null);
		}

		return Result.OK(sysUserPage);
	}





	/**
	 *   添加
	 *
	 * @param
	 * @return
	 */
	@AutoLog(value = "团队添加用户（一个或多个）")
	@ApiOperation(value="团队添加用户（一个或多个）", notes="团队添加用户（一个或多个）")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:add")
	@PostMapping(value = "/addBatch")
	public Result<String> addBatch(@RequestBody UserTeamVO userTeamVO) {

		//删除原来有的成员
		userTeamService.remove(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId,userTeamVO.getTeamId()));

		//添加
		List<UserTeam> userTeams = new ArrayList<>();
		for (String userId : userTeamVO.getUserIdList()) {
			UserTeam userTeam = new UserTeam();
			userTeam.setTeamId(userTeamVO.getTeamId());
			userTeam.setUserId(userId);
			userTeams.add(userTeam);
		}


		userTeamService.saveBatch(userTeams);
		return Result.OK("添加成功！");
	}








}
