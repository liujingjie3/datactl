package com.zjlab.dataservice.modules.message.service.impl;

import com.zjlab.dataservice.modules.message.entity.SysMessage;
import com.zjlab.dataservice.modules.message.mapper.SysMessageMapper;
import com.zjlab.dataservice.modules.message.service.ISysMessageService;
import com.zjlab.dataservice.common.system.base.service.impl.JeecgServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
