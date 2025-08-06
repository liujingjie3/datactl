package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.myspace.mapper.CollectMapper;
import com.zjlab.dataservice.modules.taskplan.mapper.MetadataMapper;
import com.zjlab.dataservice.modules.taskplan.model.entity.TaskInfoEntity;
import com.zjlab.dataservice.modules.taskplan.model.po.MetadataGf;
import com.zjlab.dataservice.modules.taskplan.model.po.TaskManagePo;
import com.zjlab.dataservice.modules.taskplan.model.vo.MetadataVo;
import com.zjlab.dataservice.modules.taskplan.service.MetadataGfService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author ZJ
 * @description 针对表【metadata_gf】的数据库操作Service实现
 * @createDate 2024-08-05 10:18:39
 */
@Service
@DS("postgre")
public class MetadataGfServiceImpl extends ServiceImpl<MetadataMapper, MetadataGf>
        implements MetadataGfService {
    @Resource
    private CollectMapper collectMapper;

    @Override
    public PageResult<MetadataVo> query(TaskInfoEntity taskInfoEntity) {
        String userId = UserThreadLocal.getUserId();
        PageResult<MetadataVo> result = new PageResult<>();
        checkParam(taskInfoEntity);
        IPage<TaskManagePo> page = new Page<>(taskInfoEntity.getPageNo(), taskInfoEntity.getPageSize());
        IPage<MetadataVo> taskList = baseMapper.query(page, taskInfoEntity);
        List<MetadataVo> listRecords = taskList.getRecords();
        for (MetadataVo metadata : listRecords) {
            // 查询是否已收藏
            boolean collected = isCollected(metadata.getId(), userId);
            // 如果已收藏，设置标志位 (你可以创建一个新的字段如 'isCollected' 来保存该状态)
            metadata.setCollected(collected);  // 假设你已在 MetadataVo 中定义了 isCollected 属性
        }
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    @Override
    public MetadataVo queryFile(String geom, LocalDateTime createTime) {
        return baseMapper.queryFile(geom, createTime);
    }

    private void checkParam(TaskInfoEntity taskInfoEntity) {
        taskInfoEntity.setPageNo(Optional.ofNullable(taskInfoEntity.getPageNo()).orElse(1));
        taskInfoEntity.setPageSize(Optional.ofNullable(taskInfoEntity.getPageSize()).orElse(10));
        taskInfoEntity.setOrderByType(Optional.ofNullable(taskInfoEntity.getOrderByType()).orElse("asc"));
    }

    public boolean isCollected(Integer id, String userId) {
//     假设你有一个 collectionMapper 或相应的查询方法，返回 Collection 表中是否存在该 id
        return collectMapper.existsByImageId(id,userId);
    }
}



