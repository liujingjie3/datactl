package com.zjlab.dataservice.modules.bench.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.bench.mapper.BenchTeamsMapper;
import com.zjlab.dataservice.modules.bench.model.entity.BenchTeam;
import com.zjlab.dataservice.modules.bench.service.BenchTeamsService;
import org.springframework.stereotype.Service;

/**
* @author ZJ
* @description 针对表【bench_teams】的数据库操作Service实现
* @createDate 2025-04-11 14:19:31
*/
@Service
public class BenchTeamsServiceImpl extends ServiceImpl<BenchTeamsMapper, BenchTeam>
    implements BenchTeamsService{

}




