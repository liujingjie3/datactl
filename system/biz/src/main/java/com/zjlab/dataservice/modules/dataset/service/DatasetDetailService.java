package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.model.dto.dataset.DatasetFileListDto;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetDetailPo;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetFileVo;
import org.springframework.stereotype.Service;


@Service
public interface DatasetDetailService extends IService<DatasetDetailPo> {

    PageResult<DatasetFileVo> qryDatasetFile(DatasetFileListDto fileListDto);
}
