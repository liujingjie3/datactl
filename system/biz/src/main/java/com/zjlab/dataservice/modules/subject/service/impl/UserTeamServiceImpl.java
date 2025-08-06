package com.zjlab.dataservice.modules.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.subject.entity.UserTeam;
import com.zjlab.dataservice.modules.subject.mapper.UserTeamMapper;
import com.zjlab.dataservice.modules.subject.service.IUserTeamService;
import org.springframework.stereotype.Service;

/**
 * @Description: 用户对应团队表
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements IUserTeamService {

}
