package com.zjlab.dataservice.modules.dataset.model.dto.dataset;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DatasetFileListDto extends PageRequest {

    @NotNull
    private Integer id;
}
