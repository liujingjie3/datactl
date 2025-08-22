package com.zjlab.dataservice.modules.tc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateCountVO {
    private long totalCount;
    private long publishedCount;
    private long unpublishedCount;
}