package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CollectImageRemoveBatchDto {

    @NotNull(message = "批量取消收藏入参不能为null")
    @NotEmpty(message = "批量取消收藏id列表不能为空数组")
    private List<@NotNull(message = "取消收藏id列表不能含有null值") Integer> ids;
}
