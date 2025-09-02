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
import com.zjlab.dataservice.modules.dataset.mapper.MetadataGfMapper;
import com.zjlab.dataservice.modules.dataset.model.po.MetadataGfPo;
import com.zjlab.dataservice.modules.myspace.enums.ImageApplyStatusEnum;
import com.zjlab.dataservice.modules.myspace.enums.ImageTypeEnum;
import com.zjlab.dataservice.modules.myspace.mapper.CollectMapper;
import com.zjlab.dataservice.modules.myspace.model.dto.collect.*;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import com.zjlab.dataservice.modules.myspace.model.vo.CollectImageDetailVo;
import com.zjlab.dataservice.modules.myspace.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectServiceImpl extends ServiceImpl<CollectMapper, CollectPo> implements CollectService {

    @Resource
    private MetadataGfMapper metadataGfMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void collectMkdir(CollectMkdirDto mkdirDto) {
        String newPath = mkdirDto.getNewPath();
        if (!StringUtils.startsWith(newPath, "/")){
            newPath = "/" + newPath;
        }
        String userId = checkUserIdExist();
        String parentPath = newPath.substring(0, newPath.lastIndexOf("/"));
        if (parentPath.isEmpty()){
            parentPath = "/";
        }
        //查询是否已存在文件夹
        QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CollectPo::getUserId, userId)
                .eq(CollectPo::getPath, newPath)
                .eq(CollectPo::getIsDir, 1)
                .eq(CollectPo::getDelFlag, 0);
        CollectPo select = baseMapper.selectOne(wrapper);
        if (select != null){
            throw new BaseException(ResultCode.COLLECT_FOLDER_EXISTED);
        }

        //创建文件夹
        String filename = newPath.substring(newPath.lastIndexOf("/") + 1);
        CollectPo mkdir = new CollectPo();
        mkdir.setPath(newPath)
                .setParentPath(parentPath)
                .setUserId(userId)
                .setFileName(filename)
                .setIsDir(1);
        mkdir.initBase(true, userId);
        int num = baseMapper.insert(mkdir);
        if (num <= 0){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void collectMovePath(CollectMovePathDto movePathDto) {
        String destPath = movePathDto.getDestPath();
        String userId = checkUserIdExist();
        checkMovePathParam(movePathDto);

        List<CollectPo> collectPoList;
        if (movePathDto.getIsDir()){       //移动文件夹
            String originPath = movePathDto.getOriginPath();
            String folderName = originPath.substring(originPath.lastIndexOf("/") + 1);
            //筛选需要更新的数据
            QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(CollectPo::getDelFlag, 0)
                    .likeRight(CollectPo::getPath, originPath)
                    .eq(CollectPo::getUserId, userId);
            collectPoList = baseMapper.selectList(wrapper);
            //整理新路径
            collectPoList.forEach(collectPo -> {
                String path = collectPo.getPath();
                String remainingPath = path.substring(originPath.length());
                String newPath = destPath + "/" + folderName + remainingPath;
                String parentPath = newPath.substring(0, newPath.lastIndexOf("/"));
                if (parentPath.isEmpty()){
                    parentPath = "/";
                }
                collectPo.setPath(newPath);
                collectPo.setParentPath(parentPath);
                collectPo.setUpdateBy(userId);
                collectPo.setUpdateTime(LocalDateTime.now());
            });
        }else {     //移动的单个文件
            List<Integer> ids = movePathDto.getIds();
            collectPoList = baseMapper.selectBatchIds(ids);
            collectPoList.forEach(collectPo -> {
                String fileName = collectPo.getFileName();
                collectPo.setPath(destPath + "/" + fileName);
                collectPo.setParentPath(destPath);
                collectPo.setUpdateBy(userId);
                collectPo.setUpdateTime(LocalDateTime.now());
            });

        }

        //批量更新
        boolean bool = updateBatchById(collectPoList);
        if (!bool){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void collectRenamePath(CollectRenamePathDto renamePathDto) {
        String originPath = renamePathDto.getOriginPath();
        String destPath = renamePathDto.getDestPath();
        String userId = checkUserIdExist();

        //重命名已有桶名目录检测
        QueryWrapper<CollectPo> checkExistWrapper = new QueryWrapper<>();
        checkExistWrapper.lambda().eq(CollectPo::getDelFlag, 0)
                .eq(CollectPo::getUserId, userId)
                .eq(CollectPo::getPath, destPath);
        List<CollectPo> existPos = baseMapper.selectList(checkExistWrapper);
        if (!existPos.isEmpty()){
            throw new BaseException(ResultCode.COLLECT_RENAME_PATH_EXIST);
        }
        
        String folderName = destPath.substring(originPath.lastIndexOf("/") + 1);
        //筛选需要更新的数据
        QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CollectPo::getDelFlag, 0)
                .likeRight(CollectPo::getPath, originPath)
                .eq(CollectPo::getUserId, userId);
        List<CollectPo> collectPoList = baseMapper.selectList(wrapper);
        //整理新路径
        collectPoList.forEach(collectPo -> {
            String path = collectPo.getPath();
            String remainingPath = path.substring(originPath.length());
            //这条记录是文件夹，改文件夹的名字
            if (remainingPath.isEmpty()){
                collectPo.setFileName(folderName);
            }
            String newPath = destPath + remainingPath;
            String parentPath = newPath.substring(0, newPath.lastIndexOf("/"));
            if (parentPath.isEmpty()){
                parentPath = "/";
            }
            collectPo.setPath(newPath);
            collectPo.setParentPath(parentPath);
            collectPo.setUpdateBy(userId);
            collectPo.setUpdateTime(LocalDateTime.now());
        });

        //批量更新
        boolean bool = updateBatchById(collectPoList);
        if (!bool){
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    private void checkMovePathParam(CollectMovePathDto movePathDto) {
        if (movePathDto.getIsDir()){
            if (movePathDto.getOriginPath() == null){
                throw new BaseException(ResultCode.COLLECT_MOVEPATH_ORIGINPATH_NULL);
            }
        }else {
            List<Integer> ids = movePathDto.getIds();
            if (ids == null || ids.contains(null)){
                throw new BaseException(ResultCode.COLLECT_MOVEPATH_IDS_CONTAIN_NULL);
            }
        }
    }

    @Override
    public PageResult<CollectImageDetailVo> qryCollectImageList(CollectImageListDto listDto) {
        if (listDto == null){
            listDto = new CollectImageListDto();
        }
        checkParam(listDto);
        String userId = checkUserIdExist();
        checkDefaultPath(listDto.getPath());

        //整理查询参数
        IPage<CollectPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<CollectPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CollectPo::getUserId, userId)
                        .eq(CollectPo::getDelFlag, 0)
                        .eq(CollectPo::getParentPath, listDto.getPath());
        //查询结果
        IPage<CollectPo> collectList = baseMapper.selectPage(page, queryWrapper);
        List<CollectPo> listRecords = collectList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)){
            return new PageResult<>();
        }
        List<CollectImageDetailVo> collect = listRecords.stream().map(collectPo -> {
            CollectImageDetailVo collectImageDetailVo = new CollectImageDetailVo();
            BeanUtils.copyProperties(collectPo, collectImageDetailVo);
            if (collectPo.getFileSize() != null){
                collectImageDetailVo.setFileSize(collectPo.getFileSize() + "GB");
            }
            collectImageDetailVo.setIsDir(collectPo.getIsDir() == 1);
            return collectImageDetailVo;
        }).collect(Collectors.toList());

        //整理返回结果格式
        PageResult<CollectImageDetailVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    //根目录默认创建default文件夹
    private void checkDefaultPath(String path) {
        if (StringUtils.equals(path, "/")){
            String userId = UserThreadLocal.getUserId();
            QueryWrapper<CollectPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CollectPo::getPath, "/default")
                    .eq(CollectPo::getIsDir, 1)
                    .eq(CollectPo::getUserId, userId)
                    .eq(CollectPo::getDelFlag,0);
            CollectPo collectPo = baseMapper.selectOne(queryWrapper);
            if (collectPo == null){
                CollectPo defaultPath = new CollectPo();
                defaultPath.setPath("/default")
                        .setParentPath("/")
                        .setUserId(userId)
                        .setFileName("default")
                        .setIsDir(1);
                defaultPath.initBase(true, userId);
                int num = baseMapper.insert(defaultPath);
                if (num <= 0){
                    throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
                }
            }
        }
    }

    private void checkParam(CollectImageListDto listDto) {
        Integer pageNo = Optional.ofNullable(listDto.getPageNo()).orElse(1);
        Integer pageSize = Optional.ofNullable(listDto.getPageSize()).orElse(10);
        listDto.setPageNo(pageNo);
        listDto.setPageSize(pageSize);
    }

//    @Override
//    public CollectImageDetailVo qryCollectImageDetail(CollectImageDetailDto detailDto) {
//        Integer imageId = detailDto.getId();
////        String userId = checkUserIdExist();
//
//        MetadataGfPo metadataGfPo = metadataGfMapper.selectById(imageId);
//        CollectImageDetailVo detailVo = new CollectImageDetailVo();
//        BeanUtils.copyProperties(metadataGfPo, detailVo);
//
//        return detailVo;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void addCollectImage(CollectImageAddDto addDto) {
        String imageIds = addDto.getImageId();
        String userId = checkUserIdExist();

        for (String imageId: imageIds.split(",")) {
            String imgId = imageId.trim();
            Integer intImgId = Integer.valueOf(imgId);
            //检查是否已收藏
            QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(CollectPo::getUserId, userId)
                    .eq(CollectPo::getImageId, intImgId)
                    .eq(CollectPo::getDelFlag, 0);
            CollectPo exist = baseMapper.selectOne(wrapper);
            // 已收藏的影像，跳过循环
            if (exist != null) {
//                throw new BaseException(ResultCode.IMAGE_ALREADY_COLLECTED);
                log.info("the image has been collected already: " + imgId);
                continue;
            }

            //查询收藏影像的信息
            MetadataGfPo metadataGfPo = metadataGfMapper.selectById(intImgId);
            //入库
            CollectPo collectPo = new CollectPo();
            collectPo.setUserId(userId)
                    .setFileName(metadataGfPo.getFileName())
                    .setFileSize(metadataGfPo.getFileSize())
                    .setPath(addDto.getPath() + "/" + metadataGfPo.getFileName())
                    .setParentPath(addDto.getPath())
                    .setIsDir(0)
                    .setImageId(intImgId)
                    // todo: 为什么强制塞GF2？？？
                    .setImageType(ImageTypeEnum.GF2.getType())
                    .setApplyStatus(ImageApplyStatusEnum.UNAPPLY.getStatus())
                    .setThumbUrl(metadataGfPo.getThumbFileLocation())
                    .setImageGsd(metadataGfPo.getImageGsd());
            collectPo.initBase(true, userId);

            int num = baseMapper.insert(collectPo);
            if (num <= 0) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
        //事务不依赖spring代理：
        // 1、解决同一个事务查询两类数据库Transactional注解，默认查询主库，不查询pg问题
        // 2、自引用CollectServiceImpl.xxx方法，循环依赖导致启动失败
//        transactionTemplate.execute(status -> {
//            try {
//                int num = baseMapper.insert(collectPo);
//                if (num <= 0) {
//                    throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
//                }
//                return null;
//            } catch (Exception e) {
//                status.setRollbackOnly();//事务回滚
//                throw e;
//            }
//        });
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void addCollectImageBatch(CollectImageAddBatchDto addBatchDto) {
//        List<Integer> imageIds = addBatchDto.getIds();
//        String userId = checkUserIdExist();
//
//        //检查是否存在已收藏的影像
//        QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
//        wrapper.lambda().eq(CollectPo::getUserId, userId)
//                .in(CollectPo::getImageId, imageIds)
//                .eq(CollectPo::getDelFlag, 0);
//        List<CollectPo> exists = baseMapper.selectList(wrapper);
//        if (!exists.isEmpty()){
//            throw new BaseException(ResultCode.CONTAIN_COLLECTED_IMAGE);
//        }
//
//        //全部收藏
//        List<CollectPo> entityList = new ArrayList<>();
//        for (Integer imageId : imageIds){
//            CollectPo collectPo = new CollectPo();
//            collectPo.setUserId(userId);
//            collectPo.setImageId(imageId);
//            collectPo.initBase(true, userId);
//            entityList.add(collectPo);
//        }
//        boolean flag = this.saveBatch(entityList);
//        if (!flag) {
//            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void removeCollectImage(CollectImageRemoveDto removeDto) {
        Integer id = removeDto.getId();
        Integer imageId = removeDto.getImageId();
        if (id == null && imageId == null){
            throw new BaseException(ResultCode.COLLECT_REMOVE_PARAM_NULL);
        }
        String userId = checkUserIdExist();

        //检查是否未收藏
        CollectPo select = null;
        //我的数据页面
        if (id != null){
            QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(CollectPo::getUserId, userId)
                    .eq(CollectPo::getId, id)
                    .eq(CollectPo::getDelFlag, 0);
            select = baseMapper.selectOne(wrapper);
        }
        //检索首页
        if (imageId != null){
            QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(CollectPo::getUserId, userId)
                    .eq(CollectPo::getImageId, imageId)
                    .eq(CollectPo::getDelFlag, 0);
            select = baseMapper.selectOne(wrapper);
        }

        if (select == null){
            throw new BaseException(ResultCode.COLLECTED_IMAGE_NOT_EXIST);
        }
        //取消收藏
        if (select.getIsDir() == 0){    //影像
            select.del(userId);
            int num = baseMapper.updateById(select);
            if (num <= 0){
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }else {     //文件夹
            removeDir(select);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void removeCollectImageBatch(CollectImageRemoveBatchDto removeBatchDto) {
        String userId = checkUserIdExist();
        List<Integer> ids = removeBatchDto.getIds();

        //检查是否存在未收藏的影像
        QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CollectPo::getUserId, userId)
                .in(CollectPo::getId, ids)
                .eq(CollectPo::getDelFlag, 0);
        List<CollectPo> selectedList = baseMapper.selectList(wrapper);
        if (selectedList.size() != ids.size()){
            throw new BaseException(ResultCode.CONTAIN_NOT_EXIST_COLLECTED_IMAGE);
        }
        List<CollectPo> imageList = selectedList.stream().filter(item -> item.getIsDir() == 0).collect(Collectors.toList());
        List<CollectPo> dirList = selectedList.stream().filter(item -> item.getIsDir() == 1).collect(Collectors.toList());
        //全部取消收藏影像
        if (!imageList.isEmpty()){
            List<CollectPo> entityList = new ArrayList<>();
            for (CollectPo select : imageList){
                select.del(userId);
                entityList.add(select);
            }
            boolean flag = this.updateBatchById(entityList);
            if (!flag) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
        if (!dirList.isEmpty()){
            for (CollectPo dirCollectPo : dirList){
                removeDir(dirCollectPo);
            }
        }
    }

    public void removeDir(CollectPo select){
        String path = select.getPath();
        String userId = UserThreadLocal.getUserId();
        QueryWrapper<CollectPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CollectPo::getDelFlag, 0)
                .likeRight(CollectPo::getPath, path)
                .eq(CollectPo::getUserId, userId);
        List<CollectPo> collectPoList = baseMapper.selectList(wrapper);
        List<CollectPo> entityList = new ArrayList<>();
        for (CollectPo collectPo : collectPoList){
            collectPo.del(userId);
            entityList.add(collectPo);
        }
        boolean flag = this.updateBatchById(entityList);
        if (!flag) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    private String checkUserIdExist(){
        String userId = UserThreadLocal.getUserId();
        if (userId == null){
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        return userId;
    }
}
