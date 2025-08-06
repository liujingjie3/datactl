package com.zjlab.dataservice.modules.myspace.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.AddOrderDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.OrderListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.ShopImageListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.UpdateOrderStatusDto;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrderPo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderMVo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderVo;

/**
* @author ZJ
* @description 针对表【myspace_order(数据订单表)】的数据库操作Service
* @createDate 2024-11-21 10:42:28
*/
@DS("postgre")
public interface MyspaceOrderService extends IService<MyspaceOrderPo> {

    PageResult<MyspaceOrderVo> qryOrderList(OrderListDto listDto);

    void addOder(AddOrderDto addDto);

    PageResult<MyspaceOrderMVo> qryOrderListm(OrderListDto listDto);

    void updateOrderStatus(UpdateOrderStatusDto updateOrderStatusDto);
}
