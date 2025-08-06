package com.zjlab.dataservice.modules.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.modules.subject.entity.ApplyRecord;
import com.zjlab.dataservice.modules.subject.mapper.ApplyRecordMapper;
import com.zjlab.dataservice.modules.subject.service.IApplyRecordService;
import org.springframework.stereotype.Service;

/**
 * @Description: 记录申请的表格
 * @Author: jeecg-boot
 * @Date:   2023-09-13
 * @Version: V1.0
 */
@Service
public class ApplyRecordServiceImpl extends ServiceImpl<ApplyRecordMapper, ApplyRecord> implements IApplyRecordService {

}
