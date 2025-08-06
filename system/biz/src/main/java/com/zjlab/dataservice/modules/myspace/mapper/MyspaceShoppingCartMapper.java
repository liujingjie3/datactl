package com.zjlab.dataservice.modules.myspace.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceShoppingCartPo;

/**
* @author ZJ
* @description 针对表【myspace_shopping_cart(数据申请列表)】的数据库操作Mapper
* @createDate 2024-11-11 14:39:37
* @Entity generator.domain.MyspaceShoppingCart
*/
@DS("postgre")
public interface MyspaceShoppingCartMapper extends BaseMapper<MyspaceShoppingCartPo> {

}




