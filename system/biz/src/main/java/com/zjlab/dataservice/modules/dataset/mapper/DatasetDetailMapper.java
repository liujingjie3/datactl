package com.zjlab.dataservice.modules.dataset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetDetailPo;
import org.apache.ibatis.annotations.Param;

public interface DatasetDetailMapper extends BaseMapper<DatasetDetailPo> {

    IPage<DatasetDetailPo> qryFileList(IPage<DatasetDetailPo> page, @Param("tableName") String tableName);
}
