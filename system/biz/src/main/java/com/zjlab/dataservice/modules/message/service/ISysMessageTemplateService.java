package com.zjlab.dataservice.modules.message.service;

import java.util.List;

import com.zjlab.dataservice.modules.message.entity.SysMessageTemplate;
import com.zjlab.dataservice.common.system.base.service.JeecgService;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends JeecgService<SysMessageTemplate> {

    /**
     * 通过模板CODE查询消息模板
     * @param code 模板CODE
     * @return
     */
    List<SysMessageTemplate> selectByCode(String code);
}
