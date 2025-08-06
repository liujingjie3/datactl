package com.zjlab.dataservice.modules.myspace.model.dto.shopcart;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddShopCartFDto {
    //影像id
    @NotNull(message = "影像id不可为空")
    private Integer id;
}
