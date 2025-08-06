package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectRenamePathDto {

    @NotNull(message = "原路径不可为空")
    private String originPath;

    @NotNull(message = "新路径不可为空")
    private String destPath;
}
