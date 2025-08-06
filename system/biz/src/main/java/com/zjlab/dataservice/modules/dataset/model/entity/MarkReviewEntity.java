package com.zjlab.dataservice.modules.dataset.model.entity;

import lombok.Data;

@Data
public class MarkReviewEntity {

    private Integer segmentReviewCnt;

    private Integer classifyReviewCnt;

    private Integer textReviewCnt;
}
