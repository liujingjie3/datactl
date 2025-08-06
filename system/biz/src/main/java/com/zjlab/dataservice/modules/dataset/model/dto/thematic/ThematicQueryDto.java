package com.zjlab.dataservice.modules.dataset.model.dto.thematic;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
public class ThematicQueryDto {
    private Integer pac;
    private String tableName;
}
