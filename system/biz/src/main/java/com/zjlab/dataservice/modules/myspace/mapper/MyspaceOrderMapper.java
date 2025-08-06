package com.zjlab.dataservice.modules.myspace.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrderPo;

/**
* @author ZJ
* @description 针对表【myspace_order(数据订单表)】的数据库操作Mapper
* @createDate 2024-11-21 10:42:28
* @Entity com.zjlab.dataservice.modules.myspace.model.po.MyspaceOrder
*/
@DS("postgre")
public interface MyspaceOrderMapper extends BaseMapper<MyspaceOrderPo> {

}




