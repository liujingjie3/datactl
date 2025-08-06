package com.zjlab.dataservice.modules.dataset.model.dto.thematic;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Data
public class ThematicMapDto extends PageRequest {

//    @NotNull(message = "省份信息不能为空")
//    private Integer province;
//
//    @NotNull(message = "专题数据类型不能为空")
//    private Integer productType;

    @NotNull(message = "专题数据ID不能为空")
    private Integer id;
}
