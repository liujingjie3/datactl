package com.zjlab.dataservice.modules.myspace.model.dto.shopcart;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddOrderDto {
    //影像id
    @NotNull(message = "影像id不可为空")
    private List<Integer> ids;

    private List<String> names;

    @NotNull(message = "数据用途说明不可为空")
    private String remark;


}
