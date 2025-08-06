package com.zjlab.dataservice.modules.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.subject.entity.Team;
import com.zjlab.dataservice.modules.subject.mapper.TeamMapper;
import com.zjlab.dataservice.modules.subject.service.ITeamService;
import org.springframework.stereotype.Service;

/**
 * @Description: 团队表
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

}
