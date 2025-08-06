package com.zjlab.dataservice.modules.dataset.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperationEnum {

    FILTER("filter", "筛选"),
    ASSIGN("assign", "分派"),
    ADD("add", "添加"),
    EDIT("edit", "编辑"),
    CHECK("check", "复查"),
    REVIEW("review", "复审"),
    CLASSIFY("classify", "分类"),
    SEGMENT("segment", "分割"),
    FILTER_METADATA("filter_metadata", "筛选原图")

    ;
    private final String type;

    private final String name;
}
