package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelAddDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelEditDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchModel;
import com.zjlab.dataservice.modules.bench.model.po.BenchModelPo;

/**
 * BenchModel 服务接口
 */
public interface BenchModelService extends IService<BenchModel> {

    /**
     * 分页查询 BenchModel 列表
     *
     * @param benchModelListDto 查询条件 DTO
     * @return 分页结果
     */
    PageResult<?> qryBenchModelList(BenchModelListDto benchModelListDto);

    /**
     * 根据 ID 查询模型详情
     *
     * @param id 模型ID
     * @return 详情视图对象
     */
    BenchModel qryBenchModelDetail(Integer id) throws Exception;

    /**
     * 根据模型名称查询模型信息
     *
     * @param name 模型名称
     * @return 模型实体
     */
    BenchModel qryDetailByName(String name);

    /**
     * 新增模型
     *
     * @param addDto 添加模型的数据传输对象
     * @return 处理结果（成功返回 ID 或提示信息）
     */
    String addBenchModel(BenchModelAddDto addDto) throws Exception;

    /**
     * 编辑模型
     *
     * @param editDto 编辑模型的数据传输对象
     * @return 处理结果（成功返回 ID 或提示信息）
     */
    String editBenchModel(BenchModelEditDto editDto) throws Exception;

    /**
     * 删除模型（逻辑删除）
     *
     * @param id 模型ID
     * @return 删除成功的条数（0 失败，1 成功）
     */
    Integer delBenchModel(Integer id) throws Exception;


}
