package com.zjlab.dataservice.modules.myspace.model.dto.collect;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CollectMovePathDto {

    private String originPath;

    @NotNull(message = "新路径不可为空")
    private String destPath;

    @NotNull(message = "文件/目录类型不可为空")
    private Boolean isDir;

    private List<Integer> ids;
}
