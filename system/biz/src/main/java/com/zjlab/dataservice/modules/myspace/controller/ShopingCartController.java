package com.zjlab.dataservice.modules.myspace.controller;

import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.*;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrderPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceShoppingCartPo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderMVo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceOrderVo;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceSubOrderVo;
import com.zjlab.dataservice.modules.myspace.service.MyspaceOrderService;
import com.zjlab.dataservice.modules.myspace.service.MyspaceShoppingCartService;
import com.zjlab.dataservice.modules.myspace.service.MyspaceSubOrderService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/myspace/shopcart")
@Slf4j
public class ShopingCartController {

    @Resource
    private MyspaceShoppingCartService myspaceShoppingCartService;
    @Resource
    private MyspaceOrderService myspaceOrderService;

    @Resource
    private MyspaceSubOrderService myspaceSubOrderService;

    @PostMapping("index/add")
    public Result addFromIndex(@RequestBody @Valid AddShopCartDto addShopCartDto){
        myspaceShoppingCartService.addFromIndex(addShopCartDto);
        return Result.OK();
    }

    @PostMapping("favourite/add")
    public Result addFromfavourite(@RequestBody @Valid AddShopCartFDto addShopCartFDto){
        myspaceShoppingCartService.addFromfavourite(addShopCartFDto);
        return Result.OK();
    }

    @PostMapping("image/list")
    public Result<PageResult<MyspaceShoppingCartPo>> qryCollectImageList(@RequestBody(required = false) ShopImageListDto listDto){
        PageResult<MyspaceShoppingCartPo> list = myspaceShoppingCartService.qryShopImageList(listDto);
        return Result.OK(list);
    }

    @PostMapping("image/remove")
    public Result removeCollectImage(@RequestBody @Valid ListImageRemoveDto removeDto){
        myspaceShoppingCartService.removeById(removeDto.getId());
        return Result.OK();
    }

    @PostMapping("image/removeBatch")
    public Result removeCollectImage(@RequestBody @Valid ListImageRemoveBatchDto removeBatchDto){
        myspaceShoppingCartService.removeBatchByIds(removeBatchDto.getIds());
        return Result.OK();
    }


    @PostMapping("order/list")
    public Result<PageResult<MyspaceOrderVo>> qryOrderImageList(@RequestBody(required = false) OrderListDto listDto){
        PageResult<MyspaceOrderVo> list = myspaceOrderService.qryOrderList(listDto);
        return Result.OK(list);
    }

    @PostMapping("order/update")
    public Result updateOrderStatus(@RequestBody(required = false) UpdateOrderStatusDto updateOrderStatusDto){
        myspaceOrderService.updateOrderStatus(updateOrderStatusDto);
        return Result.OK();
    }

    @PostMapping("order/add")
        public Result addCollectImage(@RequestBody @Valid AddOrderDto addDto){
        myspaceOrderService.addOder(addDto);
        return Result.OK();
    }


    @PostMapping("suborder/list")
    public Result<MyspaceSubOrderVo> qrySubOrderImageList(@RequestBody(required = false) SubOrderListDto listDto){
        MyspaceSubOrderVo myspaceSubOrderVo = myspaceSubOrderService.qryOrderList(listDto);
        return Result.OK(myspaceSubOrderVo);
    }


    @PostMapping("check/list")
    public Result<PageResult<MyspaceOrderMVo>> qryOrderImageListm(@RequestBody(required = false) OrderListDto listDto){
        PageResult<MyspaceOrderMVo> list = myspaceOrderService.qryOrderListm(listDto);
        return Result.OK(list);
    }


    @PostMapping("check/suborder/list")
    public Result<MyspaceSubOrderVo> checkSubOrderImageList(@RequestBody(required = false) CheckSubOrderListDto listDto){
        MyspaceSubOrderVo myspaceSubOrderVo = myspaceSubOrderService.checkSubOrderList(listDto);
        return Result.OK(myspaceSubOrderVo);
    }


    @PostMapping("check/suborder/approve")
    public Result approveSubOrderImageList(@RequestBody(required = false) ApproveDto approveDto){
        myspaceSubOrderService.approveSubOrderList(approveDto);
        return Result.OK();
    }




}
