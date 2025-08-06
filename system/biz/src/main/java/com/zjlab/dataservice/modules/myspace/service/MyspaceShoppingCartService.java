package com.zjlab.dataservice.modules.myspace.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.myspace.model.dto.shopcart.*;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceShoppingCartPo;

/**
* @author ZJ
* @description 针对表【myspace_shopping_cart(数据申请列表)】的数据库操作Service
* @createDate 2024-11-11 14:39:37
*/
@DS("postgre")
public interface MyspaceShoppingCartService extends IService<MyspaceShoppingCartPo> {

    void addFromIndex(AddShopCartDto addShopCartDto);

    void addFromfavourite(AddShopCartFDto addShopCartFDto);

    PageResult<MyspaceShoppingCartPo> qryShopImageList(ShopImageListDto listDto);

    void removeCollectImage(ListImageRemoveDto removeDto);

    void removeCollectImageBatch(ListImageRemoveBatchDto removeBatchDto);
}
