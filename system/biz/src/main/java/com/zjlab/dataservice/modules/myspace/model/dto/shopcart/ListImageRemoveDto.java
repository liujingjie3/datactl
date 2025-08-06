package com.zjlab.dataservice.modules.myspace.model.dto.shopcart;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListImageRemoveDto {

    //影像id
    @NotNull(message = "影像id不可为空")
    private Integer id;
}
