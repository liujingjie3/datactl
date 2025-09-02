package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectImageAddDto {

    //影像id
    @NotNull(message = "影像id不可为空")
    private String imageId;

    private String path = "/default";
}
