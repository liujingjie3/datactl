package com.zjlab.dataservice.modules.bench.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.mapper.BenchCapabilityMapper;
import com.zjlab.dataservice.modules.bench.model.entity.BenchCapability;
import com.zjlab.dataservice.modules.bench.model.entity.BenchDataset;
import com.zjlab.dataservice.modules.bench.service.BenchCapabilityService;
import com.zjlab.dataservice.modules.dataset.model.vo.dataset.DatasetFileVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BenchCapabilityServiceImpl extends ServiceImpl<BenchCapabilityMapper, BenchCapability> implements BenchCapabilityService {

//    @Autowired
//    private BenchCapabilityMapper benchCapabilityMapper;

    /**
     * 查询全部 BenchCapability 列表（不分页）
     *
     * @return 全部结果
     */
    @Transactional
    @Override
    public PageResult<BenchCapability> qryBenchAllCapability() {
        // 查询所有数据
        List<BenchCapability> listRecords = baseMapper.selectList(null); // 不添加条件，查询所有数据
        int total = Math.toIntExact(baseMapper.selectCount(null)); // 查询总记录数
        if (CollectionUtils.isEmpty(listRecords)){
            return new PageResult<>();
        }

        PageResult<BenchCapability> result = new PageResult<>();
        result.setPageNo(1);
        result.setPageSize(10);
        result.setTotal(total);
        result.setData(listRecords);
        return result;
    }
}
