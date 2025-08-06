package com.zjlab.dataservice.modules.myspace.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author ZJ
* @description 针对表【myspace_sub_order(子订单表)】的数据库操作Mapper
* @createDate 2024-11-25 16:02:59
* @Entity com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrder
*/
@DS("postgre")

public interface MyspaceSubOrderMapper extends BaseMapper<MyspaceSubOrderPo> {

}




