package com.zjlab.dataservice.modules.screen.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class YearSequenceVo<T> {

    private String year;

    private List<T> data;
}
