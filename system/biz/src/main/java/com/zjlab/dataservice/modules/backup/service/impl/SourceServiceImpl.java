package com.zjlab.dataservice.modules.backup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.backup.mapper.SourceMapper;
import com.zjlab.dataservice.modules.backup.model.po.SourcePo;
import com.zjlab.dataservice.modules.backup.service.SourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SourceServiceImpl extends ServiceImpl<SourceMapper, SourcePo> implements SourceService {
}
