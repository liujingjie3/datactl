package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectMkdirDto {

    @NotNull(message = "新文件夹名称不可为空")
    private String newPath;
}
