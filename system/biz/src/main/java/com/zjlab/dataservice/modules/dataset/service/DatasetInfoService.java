package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetAddDto;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetEditDto;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetListDto;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetInfoPo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetListVo;

public interface DatasetInfoService extends IService<DatasetInfoPo> {

    PageResult<DatasetListVo> qryDatasetList(DatasetListDto datasetListDto);

    DatasetDetailVo qryDatasetDetail (Integer id);

    DatasetInfoPo qryDetailByTitle(String title);

    String addDataset(DatasetAddDto addDto);

    String editDataset(DatasetEditDto editDto);

    Integer delDataset(Integer id);


}
