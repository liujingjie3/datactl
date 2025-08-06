package com.zjlab.dataservice.modules.myspace.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.myspace.enums.ImageApplyStatusEnum;
import com.zjlab.dataservice.modules.myspace.enums.OrderStatusEnum;
import com.zjlab.dataservice.modules.myspace.enums.SubOrderStatusEnum;
import com.zjlab.dataservice.modules.myspace.mapper.CollectMapper;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceShoppingCartMapper;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceSubOrderMapper;
import com.zjlab.dataservice.modules.myspace.model.dto.collect.CollectImageListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.AddOrderDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.OrderListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.ShopImageListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.UpdateOrderStatusDto;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrderPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceShoppingCartPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
import com.zjlab.dataservice.modules.myspace.model.vo.CollectImageDetailVo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderMVo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderVo;
import com.zjlab.dataservice.modules.myspace.service.MyspaceOrderService;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceOrderMapper;
import com.zjlab.dataservice.modules.myspace.service.MyspaceShoppingCartService;
import com.zjlab.dataservice.modules.myspace.service.MyspaceSubOrderService;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.service.ISysUserService;
import com.zjlab.dataservice.modules.taskplan.mapper.MetadataMapper;
import com.zjlab.dataservice.modules.taskplan.model.po.MetadataGf;
import com.zjlab.dataservice.modules.taskplan.model.vo.TaskManageVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZJ
 * @description 针对表【myspace_order(数据订单表)】的数据库操作Service实现
 * @createDate 2024-11-21 10:42:28
 */
@Service
public class MyspaceOrderServiceImpl extends ServiceImpl<MyspaceOrderMapper, MyspaceOrderPo>
        implements MyspaceOrderService {

    @Resource
    private MyspaceShoppingCartMapper myspaceShoppingCartMapper;

    @Resource
    private MyspaceShoppingCartService myspaceShoppingCartService;

    @Resource
    private MetadataMapper metadataMapper;

    @Resource
    private MyspaceSubOrderService myspacaSubOrderService;

    @Autowired
    private ISysUserService sysUserService;


    @Override
    @DS("postgre")
    public PageResult<MyspaceOrderVo> qryOrderList(OrderListDto listDto) {
        if (listDto == null) {
            listDto = new OrderListDto();
        }
        checkParam(listDto);
        String userId = checkUserIdExist();
        IPage<MyspaceOrderPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<MyspaceOrderPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceOrderPo::getUserId, userId)
                .eq(MyspaceOrderPo::getDelFlag, 0);
        //查询结果
        IPage<MyspaceOrderPo> collectList = baseMapper.selectPage(page, queryWrapper);
        List<MyspaceOrderPo> listRecords = collectList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        List<MyspaceOrderVo> collect = listRecords.stream().map(MyspaceOrderPo -> {
            MyspaceOrderVo myspaceOrderVo = MyspaceOrderVo.builder()
                    .id(MyspaceOrderPo.getId())
                    .orderId(MyspaceOrderPo.getOrderId())
                    .orderNum(MyspaceOrderPo.getOrderNum())
                    .comment(convertStringToList(MyspaceOrderPo.getComment()))
                    .userId(MyspaceOrderPo.getUserId())
                    .remark(MyspaceOrderPo.getRemark())
                    .orderStatus(MyspaceOrderPo.getOrderStatus())
                    .createTime(MyspaceOrderPo.getCreateTime())
                    .build();
            return myspaceOrderVo;
        }).collect(Collectors.toList());


        PageResult<MyspaceOrderVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS("postgre")
    public void addOder(AddOrderDto addDto) {
        String userId = checkUserIdExist();
        MyspaceOrderPo orderPo = new MyspaceOrderPo();
        String orderId = generateOrderId();
        orderPo.setOrderId(orderId);
        orderPo.setUserId(userId);
        List<Integer> ids = addDto.getIds();
        orderPo.setImageIds(ids.toString());
        orderPo.setComment(addDto.getNames().toString());
        orderPo.setOrderNum(ids.size());
        orderPo.setOrderStatus(OrderStatusEnum.DOING.getStatus());
        orderPo.initBase(true, userId);
        orderPo.setRemark(addDto.getRemark());
        int num = baseMapper.insert(orderPo);
        if (num <= 0) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
        //todo
        //更新一下收藏夹的状态
        //根据userID和imageId删除 shopcart的记录 软删除
        QueryWrapper<MyspaceShoppingCartPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MyspaceShoppingCartPo::getUserId, userId)
                .in(MyspaceShoppingCartPo::getId, ids)
                .eq(MyspaceShoppingCartPo::getDelFlag, 0);

        List<MyspaceShoppingCartPo> selectedList = myspaceShoppingCartMapper.selectList(wrapper);
        if (selectedList.size() != ids.size()) {
            throw new BaseException(ResultCode.CONTAIN_NOT_EXIST_SHOPCART_IMAGE);
        }
        List<MyspaceShoppingCartPo> entityList = new ArrayList<>();
        for (MyspaceShoppingCartPo select : selectedList) {
            select.del(userId);
            entityList.add(select);
        }
        boolean flag = myspaceShoppingCartService.updateBatchById(entityList);
        if (!flag) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }

        List<Integer> imageIdList = selectedList.stream()
                .map(MyspaceShoppingCartPo::getImageId) // 提取 imageId
                .collect(Collectors.toList());

        //把信息插入子订单表
        List<MetadataGf> metadataGfPos = metadataMapper.selectBatchIds(imageIdList);
        List<MyspaceSubOrderPo> collect = metadataGfPos.stream().map(MetadataGf -> {
            MyspaceSubOrderPo myspaceSubOrderPo = MyspaceSubOrderPo.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .imageId(MetadataGf.getId())
                    .receiveDate(MetadataGf.getReceiveTime())
                    .fileName(MetadataGf.getFileName())
                    .senser(MetadataGf.getSensorId())
                    .thumbFileLocation(MetadataGf.getThumbFileLocation())
                    .satellite(MetadataGf.getSatelliteId())
                    .level(MetadataGf.getProductLevel())
                    .cloudPercent(MetadataGf.getCloudPercent())
                    .resolution(MetadataGf.getImageGsd().toString())
                    .subOrderStatus(SubOrderStatusEnum.DOING.getStatus())
                    .build();
            myspaceSubOrderPo.initBase(true, userId);
            return myspaceSubOrderPo;
        }).collect(Collectors.toList());
        boolean flag1 = myspacaSubOrderService.saveBatch(collect);
        if (!flag1) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
    }

    @Override
    public PageResult<MyspaceOrderMVo> qryOrderListm(OrderListDto listDto) {
        if (listDto == null) {
            listDto = new OrderListDto();
        }
        Integer orderStatus = listDto.getOrderStatus();
        checkParam(listDto);
        IPage<MyspaceOrderPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<MyspaceOrderPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceOrderPo::getDelFlag, 0).eq(MyspaceOrderPo::getOrderStatus, orderStatus);
        //查询结果
        IPage<MyspaceOrderPo> collectList = baseMapper.selectPage(page, queryWrapper);
        List<MyspaceOrderPo> listRecords = collectList.getRecords();
        if (CollectionUtils.isEmpty(listRecords)) {
            return new PageResult<>();
        }
        List<MyspaceOrderMVo> collect = listRecords.stream().map(MyspaceOrderPo -> {
            SysUser user = sysUserService.getById(MyspaceOrderPo.getUserId());
            MyspaceOrderMVo myspaceOrderMVo = MyspaceOrderMVo.builder()
                    .id(MyspaceOrderPo.getId())
                    .orderId(MyspaceOrderPo.getOrderId())
                    .orderNum(MyspaceOrderPo.getOrderNum())
                    .comment(convertStringToList(MyspaceOrderPo.getComment()))
//                    .imageIds(MyspaceOrderPo.getImageIds())
                    .userId(MyspaceOrderPo.getUserId())
                    .remark(MyspaceOrderPo.getRemark())
                    .orderStatus(MyspaceOrderPo.getOrderStatus())
                    .createTime(MyspaceOrderPo.getCreateTime())
                    .talentName("之江实验室")
                    .userName(user.getRealname())
                    .build();
            return myspaceOrderMVo;
        }).collect(Collectors.toList());

        PageResult<MyspaceOrderMVo> result = new PageResult<>();
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal((int) page.getTotal());
        result.setData(collect);
        return result;
    }

    @Override
    public void updateOrderStatus(UpdateOrderStatusDto updateOrderStatusDto) {
        MyspaceOrderPo myspaceOrderPo = baseMapper.selectById(updateOrderStatusDto.getId());
        myspaceOrderPo.setOrderStatus(OrderStatusEnum.DONE.getStatus());
        int num = baseMapper.updateById(myspaceOrderPo);
        if (num <= 0) {
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

    public String generateOrderId() {
        // 获取当前日期，格式为 YYYYMMDD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        // 生成指定位数的随机数
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            randomCode.append(random.nextInt(10)); // 随机生成 0-9 的数字
        }

        // 拼接日期和随机数
        return date + randomCode.toString();
    }

    private void checkParam(OrderListDto listDto) {
        Integer pageNo = Optional.ofNullable(listDto.getPageNo()).orElse(1);
        Integer pageSize = Optional.ofNullable(listDto.getPageSize()).orElse(10);
        listDto.setPageNo(pageNo);
        listDto.setPageSize(pageSize);
    }

    public static List<String> convertStringToList(String input) {
        // 移除首尾的[]并分割字符串
        String trimmedInput = input.trim().substring(1, input.length() - 1);

        // 按照逗号分割并移除每个元素的空格
        return Arrays.stream(trimmedInput.split(","))
                .map(String::trim) // 去除空格
                .map(s -> s.replace("\"", "")) // 去除引号
                .collect(Collectors.toList());
    }
}




