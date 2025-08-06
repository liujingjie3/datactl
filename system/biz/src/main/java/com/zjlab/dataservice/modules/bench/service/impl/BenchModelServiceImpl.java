package com.zjlab.dataservice.modules.bench.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.bench.mapper.BenchModelMapper;
import com.zjlab.dataservice.modules.bench.mapper.BenchTeamsMapper;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelAddDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelEditDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchModel;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTeam;
import com.zjlab.dataservice.modules.bench.model.po.*;
import com.zjlab.dataservice.modules.bench.service.BenchModelService;
import com.zjlab.dataservice.modules.system.service.ISysDictItemService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * BenchModel 服务实现类
 */
@Service
public class BenchModelServiceImpl extends ServiceImpl<BenchModelMapper, BenchModel> implements BenchModelService {

    @Resource
    private ISysDictItemService sysDictItemService;

    @Resource
    private BenchTeamsMapper benchTeamsMapper;

    @Override
    public PageResult<?> qryBenchModelList(BenchModelListDto benchModelListDto) {
        // 参数校验
        checkParam(benchModelListDto);

        // 获取分页参数
        IPage<?> page = new Page<>(benchModelListDto.getPageNo(), benchModelListDto.getPageSize());

        // 判断 capabilityId 并选择查询方法
        IPage<?> resultPage = null;

        // capabilityId 为 null 或 空时，执行基本查询
        if (benchModelListDto.getCapabilityId() == null || "".equals(benchModelListDto.getCapabilityId())) {
            resultPage = baseMapper.qryBenchModelList((IPage<BenchModelPo>) page, benchModelListDto);
        } else {
            // 使用 Map 来映射 capabilityId 到对应的查询方法
            switch (benchModelListDto.getCapabilityId()) {
                case 1:
                    resultPage = baseMapper.qryModelCoarseList((IPage<BenchCoarsePo>) page, benchModelListDto);
                    break;
                case 2:
                    resultPage = baseMapper.qryModelFineList((IPage<BenchFinePo>) page, benchModelListDto);
                    break;
                case 3:
                    resultPage = baseMapper.qryModelLanguageList((IPage<BenchLanguagePo>) page, benchModelListDto);
                    break;
//                case 4:
//                    resultPage = baseMapper.qryModelUnderstandingList((IPage<BenchUnderstandingPo>) page, benchModelListDto);
//                    break;
                case 5:
                    resultPage = baseMapper.qryModelAnalysisList((IPage<BenchAnalysisPo>) page, benchModelListDto);
                    break;
                default:
                    // 如果 capabilityId 不匹配，返回空结果
                    return new PageResult<>();
            }
        }

        // 如果没有查询结果，返回空分页结果
        if (resultPage == null || CollectionUtils.isEmpty(resultPage.getRecords())) {
            return new PageResult<>();
        }

        return buildPageResult(page, resultPage.getRecords());
    }

    private PageResult<?> buildPageResult(IPage<?> page, List<?> listRecords) {
        PageResult<?> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    /**
     * 查询模型详情
     */
    @Override
    public BenchModel qryBenchModelDetail(Integer id) throws Exception {
        BenchModel model = baseMapper.selectModelWithTeamName(id);
        if (model == null) {
            throw new Exception("模型不存在");
        }
        return model;
//        BenchModelPo model = baseMapper.selectById(id);
//        if (model == null) {
//            throw new Exception("模型不存在");
//        }
//        return model;
    }

    /**
     * 根据模型名称查询模型信息
     *
     * @return
     */
    @Override
    public BenchModel qryDetailByName(String name) {
        QueryWrapper<BenchModel> query = new QueryWrapper<>();
        query.lambda().eq(BenchModel::getName, name).eq(BenchModel::getDelFlag, false);
        return baseMapper.selectOne(query);
    }

    /**
     * 新增模型
     */
    @Override
    public String addBenchModel(BenchModelAddDto addDto) throws Exception {
        BenchModel model = new BenchModel();
        BeanUtils.copyProperties(addDto, model);
        model.initBase(true, "admin");
        int num = baseMapper.insert(model);
        if (num <= 0) {
            throw new Exception("SQL 插入错误");
        }
        return model.getName();
    }

    /**
     * 编辑模型
     */
    @Override
    public String editBenchModel(BenchModelEditDto editDto) throws Exception {
        String userId = checkUserIdExist();
//        BenchModel model = baseMapper.selectById(editDto.getId());
        QueryWrapper<BenchModel> wrapper = new QueryWrapper<>();
        wrapper.eq("name", editDto.getName());
        BenchModel model = baseMapper.selectOne(
                wrapper);
        if (model == null) {
            throw new Exception("模型不存在");
        }
        BeanUtils.copyProperties(editDto, model);
        model.initBase(false, userId);
        int num = baseMapper.updateById(model);
        if (num <= 0) {
            throw new Exception("Model表SQL 更新错误");
        }

        if (editDto.getTeamId() != null && editDto.getTeamName() != null) {
            BenchTeam team = benchTeamsMapper.selectById(editDto.getTeamId());
            if (team != null) {
                team.setName(editDto.getTeamName());
                team.initBase(false, userId);
            }
            int i = benchTeamsMapper.updateById(team);
            if (num <= 0) {
                throw new Exception("Team表SQL 更新错误");
            }
        }
        return model.getName();
    }

    /**
     * 删除模型（逻辑删除）
     */
    @Override
    public Integer delBenchModel(Integer id) throws Exception {
        BenchModel model = new BenchModel();
        model.setId(id);
        model.del("admin");
        int num = baseMapper.updateById(model);
        if (num <= 0) {
            throw new Exception("SQL 删除错误");
        }
        return id;
    }

    /**
     * 校验参数
     */
    private void checkParam(BenchModelListDto benchModelListDto) {
        benchModelListDto.setPageNo(Optional.ofNullable(benchModelListDto.getPageNo()).orElse(1));
        benchModelListDto.setPageSize(Optional.ofNullable(benchModelListDto.getPageSize()).orElse(10));
        String field = benchModelListDto.getOrderByField();
        if (StringUtils.isBlank(field)) {

        } else {
            // 驼峰转下划线拼接
            String result = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            if (result.startsWith("_")) {
                result = result.substring(1);
            }
            benchModelListDto.setOrderByField(result);
        }
        if (StringUtils.isBlank(benchModelListDto.getOrderByType())) {
            benchModelListDto.setOrderByType("desc");
        }
    }

    private String checkUserIdExist() {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        return userId;
    }

}
