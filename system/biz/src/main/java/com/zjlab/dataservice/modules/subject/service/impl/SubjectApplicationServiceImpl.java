package com.zjlab.dataservice.modules.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.subject.entity.SubjectApplication;
import com.zjlab.dataservice.modules.subject.mapper.SubjectApplicationMapper;
import com.zjlab.dataservice.modules.subject.service.ISubjectApplicationService;
import org.springframework.stereotype.Service;

/**
 * @Description: 应用表
 * @Author: jeecg-boot
 * @Date:   2023-11-06
 * @Version: V1.0
 */
@Service
public class SubjectApplicationServiceImpl extends ServiceImpl<SubjectApplicationMapper, SubjectApplication> implements ISubjectApplicationService {

}
