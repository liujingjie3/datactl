package com.zjlab.dataservice.modules.myspace.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceOrderMapper;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceSubOrderMapper;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.ApproveDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.CheckSubOrderListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.SubOrderListDto;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrderPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceSubOrderVo;
import com.zjlab.dataservice.modules.myspace.service.MyspaceSubOrderService;
import org.springframework.stereotype.Service;
import com.zjlab.dataservice.modules.myspace.enums.SubOrderStatusEnum;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author ZJ
 * @description 针对表【myspace_sub_order(子订单表)】的数据库操作Service实现
 * @createDate 2024-11-25 16:02:59
 */
@Service
@DS("postgre")

public class MyspaceSubOrderServiceImpl extends ServiceImpl<MyspaceSubOrderMapper, MyspaceSubOrderPo>
        implements MyspaceSubOrderService {
    @Resource
    private MyspaceOrderMapper myspaceOrderMapper;

    @Resource
    private TimingTaskServiceImpl timingTaskService;

    @Override
    public MyspaceSubOrderVo qryOrderList(SubOrderListDto listDto) {
//        checkParam(listDto);
        String userId = checkUserIdExist();
        String orderId = listDto.getOrderId();
        //查询

        QueryWrapper<MyspaceOrderPo> queryWrappera = new QueryWrapper<>();
        queryWrappera.lambda().eq(MyspaceOrderPo::getUserId, userId)
                .eq(MyspaceOrderPo::getOrderId, orderId)
                .eq(MyspaceOrderPo::getDelFlag, 0);
        MyspaceOrderPo orderPo = myspaceOrderMapper.selectOne(queryWrappera);
        String remark = orderPo.getRemark();
        Integer orderStatus = orderPo.getOrderStatus();
        LocalDateTime orderTime = orderPo.getCreateTime();

//        IPage<MyspaceSubOrderPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<MyspaceSubOrderPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceSubOrderPo::getUserId, userId)
                .eq(MyspaceSubOrderPo::getOrderId, orderId)
                .eq(MyspaceSubOrderPo::getDelFlag, 0);
        //查询结果
        List<MyspaceSubOrderPo> collectList = baseMapper.selectList(queryWrapper);
        MyspaceSubOrderVo myspaceSubOrderVo = MyspaceSubOrderVo.builder()
                .list(collectList)
                .orderStatus(orderStatus)
                .remark(remark)
                .orderTime(orderTime)
                .build();
        return myspaceSubOrderVo;
    }

    @Override
    public MyspaceSubOrderVo checkSubOrderList(CheckSubOrderListDto listDto) {
//        manageCheckParam(listDto);
//        String manege_userId = checkUserIdExist();
        String orderId = listDto.getOrderId();
        String userId = listDto.getUserId();
        //查询
        QueryWrapper<MyspaceOrderPo> queryWrappera = new QueryWrapper<>();
        queryWrappera.lambda().eq(MyspaceOrderPo::getUserId, userId)
                .eq(MyspaceOrderPo::getOrderId, orderId)
                .eq(MyspaceOrderPo::getDelFlag, 0);
        MyspaceOrderPo orderPo = myspaceOrderMapper.selectOne(queryWrappera);
        String remark = orderPo.getRemark();
        Integer orderStatus = orderPo.getOrderStatus();
        LocalDateTime orderTime = orderPo.getCreateTime();

//        IPage<MyspaceSubOrderPo> page = new Page<>(listDto.getPageNo(), listDto.getPageSize());
        QueryWrapper<MyspaceSubOrderPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyspaceSubOrderPo::getUserId, userId)
                .eq(MyspaceSubOrderPo::getOrderId, orderId)
                .eq(MyspaceSubOrderPo::getDelFlag, 0);
        //查询结果
        List<MyspaceSubOrderPo> collectList = baseMapper.selectList(queryWrapper);
//        List<MyspaceSubOrderPo> listRecords = collectList.getRecords();
        MyspaceSubOrderVo myspaceSubOrderVo = MyspaceSubOrderVo.builder()
                .list(collectList)
                .orderStatus(orderStatus)
                .remark(remark)
                .orderTime(orderTime)
                .build();
        return myspaceSubOrderVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveSubOrderList(ApproveDto approveDto) {
        String manege_userId = checkUserIdExist();
        Integer id = approveDto.getId();
        MyspaceSubOrderPo myspaceSubOrderPo = baseMapper.selectById(id);
        //订单ID
        Integer subOrderStatus = approveDto.getSubOrderStatus();
        String approvalOpinion = approveDto.getApprovalOpinion();

        if (subOrderStatus == SubOrderStatusEnum.DONE.getStatus()) {
            //同意
            String fileUrl = approveDto.getFileUrl();
            myspaceSubOrderPo.setSubOrderStatus(subOrderStatus);
            myspaceSubOrderPo.setApprovalOpinion(approvalOpinion);
            myspaceSubOrderPo.setApprover(manege_userId);
            myspaceSubOrderPo.setFileUrl(fileUrl);
            myspaceSubOrderPo.setApprovalTime(LocalDateTime.now());
            timingTaskService.changeSubOrderStatus(id, LocalDateTime.now());
            int num = baseMapper.updateById(myspaceSubOrderPo);
            if (num <= 0) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }

        } else if (subOrderStatus == SubOrderStatusEnum.UNDONE.getStatus()) {
            //拒绝
            myspaceSubOrderPo.setSubOrderStatus(subOrderStatus);
            myspaceSubOrderPo.setApprovalOpinion(approvalOpinion);
            myspaceSubOrderPo.setApprover(manege_userId);
            myspaceSubOrderPo.setApprovalTime(LocalDateTime.now());
            int num = baseMapper.updateById(myspaceSubOrderPo);
            if (num <= 0) {
                throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
            }
        }
    }

    private String checkUserIdExist() {
        String userId = UserThreadLocal.getUserId();
        if (userId == null) {
            throw new BaseException(ResultCode.USERID_IS_NULL);
        }
        return userId;
    }

    private void checkParam(SubOrderListDto listDto) {
        Integer pageNo = Optional.ofNullable(listDto.getPageNo()).orElse(1);
        Integer pageSize = Optional.ofNullable(listDto.getPageSize()).orElse(6);
        listDto.setPageNo(pageNo);
        listDto.setPageSize(pageSize);
    }

    private void manageCheckParam(CheckSubOrderListDto listDto) {
        Integer pageNo = Optional.ofNullable(listDto.getPageNo()).orElse(1);
        Integer pageSize = Optional.ofNullable(listDto.getPageSize()).orElse(6);
        listDto.setPageNo(pageNo);
        listDto.setPageSize(pageSize);
    }
}




