package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

@Data
public class CollectImageRemoveDto {

    //影像id
//    @NotNull(message = "收藏id不可为空")
    private Integer id;

    private Integer imageId;
}
