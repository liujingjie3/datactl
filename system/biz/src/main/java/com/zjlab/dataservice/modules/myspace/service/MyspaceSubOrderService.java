package com.zjlab.dataservice.modules.myspace.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.ApproveDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.CheckSubOrderListDto;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.SubOrderListDto;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.myspace.model.vo.MyspaceSubOrderVo;

/**
 * @author ZJ
 * @description 针对表【myspace_sub_order(子订单表)】的数据库操作Service
 * @createDate 2024-11-25 16:02:59
 */
@DS("postgre")
public interface MyspaceSubOrderService extends IService<MyspaceSubOrderPo> {

    MyspaceSubOrderVo qryOrderList(SubOrderListDto listDto);

    MyspaceSubOrderVo checkSubOrderList(CheckSubOrderListDto listDto);

    void approveSubOrderList(ApproveDto approveDto);
}
