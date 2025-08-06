package com.zjlab.dataservice.modules.subject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjlab.dataservice.common.constant.enums.ApplyStatusEnum;
import com.zjlab.dataservice.modules.subject.entity.ApplyRecord;
import com.zjlab.dataservice.modules.subject.entity.UserTeam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.common.aspect.annotation.AutoLog;
import com.zjlab.dataservice.common.system.query.QueryGenerator;
import com.zjlab.dataservice.modules.subject.service.IApplyRecordService;
import com.zjlab.dataservice.modules.subject.service.IUserTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags="授权表")
@RestController
@RequestMapping("/subject/empower")
@Slf4j
public class EmpowerController {

    @Autowired
    private IApplyRecordService applyRecordService;
    @Autowired
    private IUserTeamService userTeamService;

    /**
     *   添加
     *
     * @param applyRecord
     * @return
     */
    @AutoLog(value = "申请团队")
    @ApiOperation(value="申请团队", notes="申请团队")
    //@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:add")
    @PostMapping(value = "/apply")
    public Result<String> add(@RequestBody ApplyRecord applyRecord) {
        applyRecordService.save(applyRecord);
        return Result.OK("添加成功！");
    }




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
    @ApiOperation(value="分页查询申请记录", notes="分页查询申请记录")
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



    /**
     *  同意或者拒绝
     *
     * @param applyRecord
     * @return
     */
    @AutoLog(value = "同意或者拒绝")
    @ApiOperation(value="同意或者拒绝", notes="同意或者拒绝")
    //@RequiresPermissions("org.jeecg.modules.demo:subject_apply_record:edit")
    @RequestMapping(value = "/approve", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> approve(@RequestBody ApplyRecord applyRecord) {
        applyRecordService.updateById(applyRecord);

        if (ApplyStatusEnum.AGREED.getType()==applyRecord.getApplyStatus()){
            UserTeam userTeam = new UserTeam();
            userTeam.setTeamId(applyRecord.getTeamId());
            userTeam.setUserId(applyRecord.getUserId());
            userTeamService.save(userTeam);
        }

        return Result.OK("成功!");
    }







}
