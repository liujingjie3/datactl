package com.zjlab.dataservice.modules.screen.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TimeSequenceVo {

    private String year;

    private long rawData;

    private long GF2;

    private long GF3;
}
