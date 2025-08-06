package com.zjlab.dataservice.modules.cart.controller;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.cart.entity.SysFavorite;
import com.zjlab.dataservice.modules.cart.mapper.SysFavoriteMapper;
import com.zjlab.dataservice.modules.cart.model.FavoriteDto;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/cart/favorite")
@Api(tags = "收藏夹")
public class CartFavoriteController {

    @Autowired
    private SysFavoriteMapper sysFavoriteMapper;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<JSONObject> add(@RequestBody FavoriteDto favoriteDto) {
        SysFavorite sysFavorite = SysFavorite.builder().build();
        this.sysFavoriteMapper.insert(sysFavorite);

        return Result.OK();
    }
}