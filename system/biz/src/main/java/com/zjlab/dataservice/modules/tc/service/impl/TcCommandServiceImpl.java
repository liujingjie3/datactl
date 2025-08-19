package com.zjlab.dataservice.modules.tc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.tc.mapper.TcCommandMapper;
import com.zjlab.dataservice.modules.tc.model.entity.TcCommand;
import com.zjlab.dataservice.modules.tc.service.TcCommandService;

import org.springframework.stereotype.Service;

@Service
public class TcCommandServiceImpl extends ServiceImpl<TcCommandMapper, TcCommand>
    implements TcCommandService {

}




