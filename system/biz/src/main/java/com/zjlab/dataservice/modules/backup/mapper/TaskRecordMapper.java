package com.zjlab.dataservice.modules.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjlab.dataservice.modules.backup.model.entity.TaskRecordListEntity;
import com.zjlab.dataservice.modules.backup.model.po.TaskRecordPo;
import org.apache.ibatis.annotations.Param;

public interface TaskRecordMapper extends BaseMapper<TaskRecordPo> {

    IPage<TaskRecordListEntity> qryTaskRecordList(IPage<TaskRecordListEntity> page, @Param("request") TaskRecordListEntity request);
}
