package com.zjlab.dataservice.modules.subject.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjlab.dataservice.modules.subject.entity.Team;
import org.apache.commons.lang3.StringUtils;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.system.query.QueryGenerator;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.zjlab.dataservice.modules.subject.service.ITeamService;
import com.zjlab.dataservice.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;

 /**
 * @Description: 团队表
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Api(tags="团队表")
@RestController
@RequestMapping("/subject/team")
@Slf4j
public class TeamController extends JeecgController<Team, ITeamService> {
	@Autowired
	private ITeamService teamService;
	
	/**
	 * 分页列表查询
	 *
	 * @param team
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "团队表-分页列表查询")
	@ApiOperation(value="团队表-分页列表查询", notes="团队表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Team>> queryPageList(Team team,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Team> queryWrapper = QueryGenerator.initQueryWrapper(team, req.getParameterMap());
		Page<Team> page = new Page<Team>(pageNo, pageSize);
		IPage<Team> pageList = teamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param team
	 * @return
	 */
	@AutoLog(value = "团队表-添加")
	@ApiOperation(value="团队表-添加", notes="团队表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_team:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Team team) {
		//判断编码是否重复
		List<Team> list = teamService.list(new LambdaQueryWrapper<Team>().select(Team::getTeamCode));
		for (int i = 0; i < list.size(); i++) {
			if (StringUtils.isNotEmpty(list.get(i).getTeamCode())){
				if (list.get(i).getTeamCode().equals(team.getTeamCode())){
					return Result.error("团队编码重复");
				}
			}

		}
		teamService.save(team);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param team
	 * @return
	 */
	@AutoLog(value = "团队表-编辑")
	@ApiOperation(value="团队表-编辑", notes="团队表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_team:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Team team) {
		//判断编码是否重复
		List<Team> list = teamService.list(new LambdaQueryWrapper<Team>().select(Team::getTeamCode).ne(Team::getId,team.getId()));
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTeamCode().equals(team.getTeamCode())){
				return Result.error("团队编码重复");
			}
		}
		teamService.updateById(team);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "团队表-通过id删除")
	@ApiOperation(value="团队表-通过id删除", notes="团队表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_team:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		teamService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "团队表-批量删除")
	@ApiOperation(value="团队表-批量删除", notes="团队表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:subject_team:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.teamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "团队表-通过id查询")
	@ApiOperation(value="团队表-通过id查询", notes="团队表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Team> queryById(@RequestParam(name="id",required=true) String id) {
		Team team = teamService.getById(id);
		if(team==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(team);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param team
    */
    //@RequiresPermissions("org.jeecg.modules.demo:subject_team:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Team team) {
        return super.exportXls(request, team, Team.class, "团队表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("subject_team:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Team.class);
    }







}
