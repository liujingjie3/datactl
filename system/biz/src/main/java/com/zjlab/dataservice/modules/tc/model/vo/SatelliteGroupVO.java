package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 卫星分组信息
 */
@Data
public class SatelliteGroupVO implements Serializable {

    private static final long serialVersionUID = 806291365827527820L;
    /** 星座或分组名称 */
    @NotBlank(message = "group不能为空")
    private String group;
    /** 分组下的卫星ID集合 */
    @NotEmpty(message = "satIds不能为空")
    private List<@NotBlank(message = "satId不能为空") String> satIds;
}
