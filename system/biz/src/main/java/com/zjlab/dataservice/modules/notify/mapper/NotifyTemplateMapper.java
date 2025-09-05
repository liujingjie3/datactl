package com.zjlab.dataservice.modules.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模板 Mapper
 */
@Mapper
public interface NotifyTemplateMapper extends BaseMapper<NotifyTemplate> {

    NotifyTemplate selectByBizAndChannel(@Param("bizType") byte bizType,
                                         @Param("channel") byte channel);
}

