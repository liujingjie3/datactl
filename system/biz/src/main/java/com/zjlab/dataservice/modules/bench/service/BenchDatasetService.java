package com.zjlab.dataservice.modules.bench.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelAddDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchModelEditDto;
import com.zjlab.dataservice.modules.bench.model.dto.BenchDatasetListDto;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;

public interface BenchDatasetService extends IService<BenchDataset> {
    /**
     * 分页查询 BenchDatasets 列表
     *
     * @param benchDatasetListDto 查询条件 DTO
     * @return 分页结果
     */
    PageResult<BenchDataset> qryBenchDatasetList(BenchDatasetListDto benchDatasetListDto);

    /**
     * 根据 ID 查询数据集详情
     *
     * @param id 数据集ID
     * @return 详情视图对象
     */
    BenchDataset qryBenchDatasetDetail(Integer id) throws Exception;

    /**
     * 根据数据集名称查询数据集信息
     *
     * @param name 数据集名称
     * @return 数据集实体
     */
    BenchDataset qryDetailByName(String name);

    /**
     * 新增数据集
     *
     * @param dataset 添加数据集的数据传输对象
     * @return 处理结果（成功返回 ID 或提示信息）
     */
    String addBenchDataset(BenchDataset dataset) throws Exception;

    /**
     * 编辑数据集
     *
     * @param dataset 编辑数据集的数据传输对象
     * @return 处理结果（成功返回 ID 或提示信息）
     */
    String editBenchDataset(BenchDataset dataset) throws Exception;

    /**
     * 删除数据集（逻辑删除）
     *
     * @param id 数据集ID
     * @return 删除成功的条数（0 失败，1 成功）
     */
    Integer delBenchDataset(Integer id) throws Exception;
}
