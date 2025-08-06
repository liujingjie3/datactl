package com.zjlab.dataservice.modules.myspace.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.myspace.enums.ImageApplyStatusEnum;
import com.zjlab.dataservice.modules.myspace.mapper.CollectMapper;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceShoppingCartMapper;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.*;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceShoppingCartPo;
import com.zjlab.dataservice.modules.myspace.service.MyspaceShoppingCartService;
import com.zjlab.dataservice.modules.taskplan.mapper.MetadataMapper;
import com.zjlab.dataservice.modules.taskplan.model.po.MetadataGf;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【myspace_shopping_cart(数据申请列表)】的数据库操作Service实现
 * @createDate 2024-11-11 14:39:37
 */
@Service
@DS("postgre")
public class MyspaceShoppingCartServiceImpl extends ServiceImpl<MyspaceShoppingCartMapper, MyspaceShoppingCartPo>
        implements MyspaceShoppingCartService {
    @Resource
    private MetadataMapper metadataMapper;

    @Resource
    private CollectMapper collectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFromIndex(AddShopCartDto addShopCartDto) {
        List<Integer> imageIds = addShopCartDto.getIds();
        String userId = checkUserIdExist();
        //检查是否存在已在申请列表的影像，有的就剔除
        QueryWrapper<MyspaceShoppingCartPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MyspaceShoppingCartPo::getUserId, userId)
                .in(MyspaceShoppingCartPo::getImageId, imageIds)
                .eq(MyspaceShoppingCartPo::getDelFlag, 0);
        List<MyspaceShoppingCartPo> exists = baseMapper.selectList(wrapper);
        // 获取 exists 列表中的 imageId 集合
        Set<Integer> existsImageIds = exists.stream()
                .map(MyspaceShoppingCartPo::getImageId)
                .collect(Collectors.toSet());
        // 过滤掉在 exists 中出现的 imageId
        List<Integer> filteredImageIds = imageIds.stream()
                .filter(id -> !existsImageIds.contains(id))
                .collect(Collectors.toList());
        if (!filteredImageIds.isEmpty()) {
            List<MetadataGf> metadataGfPos = metadataMapper.selectBatchIds(filteredImageIds);
            List<MyspaceShoppingCartPo> collect = metadataGfPos.stream().map(MetadataGf -> {
                MyspaceShoppingCartPo myspaceShoppingCartPo = MyspaceShoppingCartPo.builder()
                        .userId(userId)
                        .imageId(MetadataGf.getId())
                        .fileName(MetadataGf.getFileName())
                        .thumbFileLocation(MetadataGf.getThumbFileLocation())
                        .satellite(MetadataGf.getSatelliteId())
                        .senser(MetadataGf.getSensorId())
                        .resolution(MetadataGf.getImageGsd().toString())
                        .level(MetadataGf.getProductLevel())
                        .build();
                myspaceShoppingCartPo.initBase(true, userId);
                return myspaceShoppingCartPo;
            }).collect(Collectors.toList());

            boolean flag = this.saveBatch(collect);
            if (!flag) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFromfavourite(AddShopCartFDto addShopCartFDto) {
        Integer id = addShopCartFDto.getId();
        CollectPo collectPo = collectMapper.selectById(id);
        Integer imageId = collectPo.getImageId();
        String userId = checkUserIdExist();
        QueryWrapper<MyspaceShoppingCartPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceShoppingCartPo::getImageId, imageId).eq(MyspaceShoppingCartPo::getUserId, userId);
        MyspaceShoppingCartPo myspaceShoppingCartPo = baseMapper.selectOne(queryWrapper);
        // 检查是否存在
        if (myspaceShoppingCartPo != null) {
            myspaceShoppingCartPo.initBase(true, userId);
            int num = baseMapper.updateById(myspaceShoppingCartPo);
            if (num <= 0) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }

        } else {
            // 数据不存在，加入申请列表
            MetadataGf metadataGf = metadataMapper.selectById(imageId);
            MyspaceShoppingCartPo ShoppingCartPo = MyspaceShoppingCartPo.builder()
                    .userId(userId)
                    .imageId(metadataGf.getId())
                    .fileName(metadataGf.getFileName())
                    .thumbFileLocation(metadataGf.getThumbFileLocation())
                    .satellite(metadataGf.getSatelliteId())
                    .senser(metadataGf.getSensorId())
                    .resolution(metadataGf.getImageGsd().toString())
                    .level(metadataGf.getProductLevel())
                    .build();
            ShoppingCartPo.initBase(true, userId);

            int num = baseMapper.insert(ShoppingCartPo);
            if (num <= 0) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
    }

    @Override
    public PageResult<MyspaceShoppingCartPo> qryShopImageList(ShopImageListDto listDto) {

        if (listDto == null) {
            listDto = new ShopImageListDto();
        }
        checkParam(listDto);
        String userId = checkUserIdExist();
        //整理查询参数
        IPage<MyspaceShoppingCartPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<MyspaceShoppingCartPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceShoppingCartPo::getUserId, userId)
                .eq(MyspaceShoppingCartPo::getDelFlag, 0);
        //查询结果
        IPage<MyspaceShoppingCartPo> collectList = baseMapper.selectPage(page, queryWrapper);
        List<MyspaceShoppingCartPo> listRecords = collectList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        //整理返回结果格式
        PageResult<MyspaceShoppingCartPo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(listRecords);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCollectImage(ListImageRemoveDto removeDto) {
        Integer imageId = removeDto.getId();
        String userId = checkUserIdExist();

        //检查是否在申请列表
        QueryWrapper<MyspaceShoppingCartPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MyspaceShoppingCartPo::getUserId, userId)
                .eq(MyspaceShoppingCartPo::getImageId, imageId)
                .eq(MyspaceShoppingCartPo::getDelFlag, 0);
        MyspaceShoppingCartPo select = baseMapper.selectOne(wrapper);
        if (select == null) {
            throw new BaseException(ResultCode.COLLECTED_IMAGE_NOT_EXIST);
        }

        select.del(userId);
        int num = baseMapper.updateById(select);
        if (num <= 0) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCollectImageBatch(ListImageRemoveBatchDto removeBatchDto) {
        String userId = checkUserIdExist();
        List<Integer> imageIds = removeBatchDto.getIds();

        //检查是否存在未收藏的影像
        QueryWrapper<MyspaceShoppingCartPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MyspaceShoppingCartPo::getUserId, userId)
                .in(MyspaceShoppingCartPo::getImageId, imageIds)
                .eq(MyspaceShoppingCartPo::getDelFlag, 0);
        List<MyspaceShoppingCartPo> selectedList = baseMapper.selectList(wrapper);
        if (selectedList.size() != imageIds.size()) {
            throw new BaseException(ResultCode.CONTAIN_NOT_EXIST_SHOPCART_IMAGE);
        }



        //全部取消收藏
        List<MyspaceShoppingCartPo> entityList = new ArrayList<>();
        for (MyspaceShoppingCartPo select : selectedList) {
            select.del(userId);
            entityList.add(select);
        }
        boolean flag = this.updateBatchById(entityList);
        if (!flag) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    private String checkUserIdExist() {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        return userId;
    }

    private void checkParam(ShopImageListDto listDto) {
        Integer pageNo = Optional.ofNullable(listDto.getPageNo()).orElse(1);
        Integer pageSize = Optional.ofNullable(listDto.getPageSize()).orElse(10);
        listDto.setPageNo(pageNo);
        listDto.setPageSize(pageSize);
    }

}


